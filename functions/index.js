// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addMessage = functions.https.onCall((data, context) => {
    
  return data;
});

// Listens for new messages added to /messages/:pushId/original and creates an
// uppercase version of the message to /messages/:pushId/uppercase
exports.makeUppercase = functions.database.ref('/messages/{pushId}/original')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const original = snapshot.val();
      console.log('Uppercasing', context.params.pushId, original);
      const uppercase = original.toUpperCase();
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return snapshot.ref.parent.child('uppercase').set(uppercase);
    });

exports.sendFollowerNotification = functions.database.ref('/followers/{followedUid}/{followerUid}')
    .onWrite(async (change, context) => {
      const followerUid = context.params.followerUid;
      const followedUid = context.params.followedUid;
      // If un-follow we exit the function.
      if (!change.after.val()) {
        return console.log('User ', followerUid, 'un-followed user', followedUid);
      }
      console.log('We have a new follower UID:', followerUid, 'for user:', followedUid);

      // Get the list of device notification tokens.
      const getDeviceTokensPromise = admin.database()
          .ref(`/users/${followedUid}/notificationTokens`).once('value');

      // Get the follower profile.
      const getFollowerProfilePromise = admin.auth().getUser(followerUid);

      // The snapshot to the user's tokens.
      let tokensSnapshot;

      // The array containing all the user's tokens.
      let tokens;

      const results = await Promise.all([getDeviceTokensPromise, getFollowerProfilePromise]);
      tokensSnapshot = results[0];
      const follower = results[1];

      // Check if there are any device tokens.
      if (!tokensSnapshot.hasChildren()) {
        return console.log('There are no notification tokens to send to.');
      }
      console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
      console.log('Fetched follower profile', follower);

      // Notification details.
      const payload = {
        notification: {
          title: 'You have a new follower!',
          body: `${follower.displayName} is now following you.`,
          icon: follower.photoURL
        }
      };

      // Listing all tokens as an array.
      tokens = Object.keys(tokensSnapshot.val());
      // Send notifications to all tokens.
      const response = await admin.messaging().sendToDevice(tokens, payload);
      // For each message check if there was an error.
      const tokensToRemove = [];
      response.results.forEach((result, index) => {
        const error = result.error;
        if (error) {
          console.error('Failure sending notification to', tokens[index], error);
          // Cleanup the tokens who are not registered anymore.
          if (error.code === 'messaging/invalid-registration-token' ||
              error.code === 'messaging/registration-token-not-registered') {
            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
          }
        }
      });
      return Promise.all(tokensToRemove);
    });


// Keeps track of the length of the 'likes' child list in a separate property.
exports.countlikechange = functions.database.ref('/posts/{postid}/likes/{likeid}').onWrite(
    async (change) => {
      const collectionRef = change.after.ref.parent;
      const countRef = collectionRef.parent.child('likes_count');

      let increment;
      if (change.after.exists() && !change.before.exists()) {
        increment = 1;
      } else if (!change.after.exists() && change.before.exists()) {
        increment = -1;
      } else {
        return null;
      }

      // Return the promise from countRef.transaction() so our function
      // waits for this async event to complete before it exits.
      await countRef.transaction((current) => {
        return (current || 0) + increment;
      });
      console.log('Counter updated.');
      return null;
    });

// If the number of likes gets deleted, recount the number of likes
exports.recountlikes = functions.database.ref('/posts/{postid}/likes_count').onDelete(async (snap) => {
  const counterRef = snap.ref;
  const collectionRef = counterRef.parent.child('likes');

  // Return the promise from counterRef.set() so our function
  // waits for this async event to complete before it exits.
  const messagesData = await collectionRef.once('value');
  return await counterRef.set(messagesData.numChildren());
});