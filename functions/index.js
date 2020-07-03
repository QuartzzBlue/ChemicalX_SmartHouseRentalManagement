
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendPaymentNotification = functions.database.ref('/credit/{unitUid}/{creditUid}')
    .onWrite(async (change, context) => {
      const unitUid = context.params.unitUid;
      
      console.log('new Payment Updated : ',unitUid);

      //Get the building
      const buildingUid = admin.database().ref(`/registeredTenants/${unitUid}/buildingID`).once('value');
      console.log('buildingUid : '+(await buildingUid).val())


      // const landlordKey = admin.database().ref(`/buildings/${userUid}/${buildingUid}`).parent;
      // console.log("landlordKey : ",landlordKey);

      // //Get the landlord token
      // const landlordToken = admin.database().ref(`/user/`+landlordKey).child('token').once('value');
      // console.log("landlordToken : ",landlordToken);
      // //const results = await Promise.all(land); 

      return null;
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


// Get a reference to the database service
var database = admin.database();

function produceCredits() {
  var todayDate = new Date();
  var theDay = todayDate.getDate();
  
  var today = todayDate.getFullYear() + '.' + (todayDate.getMonth()+1) + '.' + theDay; 

    database.ref('payday/'+theDay+'/').once('value',function(snapshot) {
      var newCredits = [];
      snapshot.forEach(function(childSnapshot) {
        var childData = childSnapshot.val();
        var unitID = childSnapshot.key;
        console.log("unitID : " + unitID);

        newCredits.push(childData);
      });
      
      for(var key in newCredits) {
        var temp = newCredits[key];
        var creditKey = database.ref('credit/' + temp.unitID+'/').push().key;
        console.log("creditKey : " + creditKey);
        database.ref('credit/'+temp.unitID+'/'+creditKey+'/').set({"unitID" : temp.unitID, "creditID" : creditKey, "credit" : temp.credit, "status" : "미납", "billingDate" : today});

        }
    });
  

};

exports.scheduledFunctionCrontab = functions.pubsub.schedule('1 0 * * *')
  .timeZone('Asia/Seoul') // Users can choose timezone
  .onRun((context) => {
  produceCredits();
  console.log('This will be run every day at 0:01 AM!');
  return null;
});

// exports.scheduledFunctionCrontab = functions.pubsub.schedule('1 0 * * *')
//   .timeZone('America/New_York' + 9) // Users can choose timezone
//   .onRun((context) => {
//   produceCredits();
//   console.log('This will be run every day at 0:01 AM!');
//   return null;
// });

// exports.scheduledFunction = functions.pubsub.schedule('every 1 minutes').onRun((context) => {
//   produceCredits();
//   console.log('This will be run every 1 minutes!');
//   return null;
// });
