import * as functions from 'firebase-functions';
//import * as moment from 'moment';

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript


//const moment = require('moment');

//let now = moment().format('LLLL');


// The Firebase Admin SDK to access the Firebase Realtime Database.
// const admin = require('firebase-admin');
// admin.initializeApp();


// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
// exports.addMessage = functions.https.onRequest(async (req, res) => {
//     // Grab the text parameter.
//     const original = req.query.text;
//     // Push the new message into the Realtime Database using the Firebase Admin SDK.
//     const snapshot = await admin.database().ref('/adminMessages').push({original: original});
//     // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
//     res.redirect(303, snapshot.ref.toString());
//   });
/*
 * author: @JHM9191
 * detail: This works in HTTP method. {produced url} + {?text=test}
 */




// export const helloWorld = functions.https.onRequest((request, response) => {
//     console.log("test");
//     // const now = moment();
//     // const dateFormatted = now.format('DDMMYYYY');
//     // response.send("Today is " + dateFormatted);
//     response.send('TEST');
// });

// export const monthlyPayment = functions.database
// .ref('/units/{buildingID}/{unitID}')
// .onUpdate((change, context) => {
//     const today = new Date();
//     console.log(today);
//     const after = change.after.val();
//     console.log(after.payDay);
//     return null;
// })


export const monthlyPaymentCreate = functions.database
.ref('/units/{buildingID}/{unitID}')
.onCreate((snapshot, context) => {
    const unitID = context.params.unitID
    console.log(`unitID : ${unitID}`)
    const unitData = snapshot.val() 
    const payDay = unitData.payDay
    console.log(`payDay : ${payDay}`)
    return null;
})

exports.scheduledFunctionCrontab = functions.pubsub.schedule('00 00 * * *')
  //.timeZone('America/New_York') // Users can choose timezone - default is America/Los_Angeles
  .timeZone('Asia/Seoul')
  .onRun((context) => {
  console.log('This is scheduledFunctionCrontab()');



  return null;
});