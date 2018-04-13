const functions = require('firebase-functions');
const admin = require('firebase-admin');
const Filter = require('bad-words');
const osmosis = require('osmosis');



const badWordsFilter = new Filter();
admin.initializeApp(functions.config().firebase);
var db = admin.firestore();
exports.checkReview = functions.firestore
 .document('/speakers/{speakerId}/ratings/{ratingId}')
    .onCreate((event) => {
       const data = event.data.data().text;
	   return event.data.ref.set({
            text: badWordsFilter.clean(data)
        }, {merge: true});
    });
	
function parse(url) {
	console.log('Parsing:' + url);
	var docRef = db.collection('speakers');
	 osmosis
    .get(url)
	.find('h1[@class="text--h1_2"]')
	.set('section')
	.find('//div[@class="lecture"]')
	.set({'img' : 'img@src',
	       'speaker' : 'img@alt',
		   'name': 'div[@class="lecture--name"]',
		   'company': 'div[@class="lecture--speaker"]'
		   })
    .data(function(document) {
		var temp=(document.company).substring((document.company).indexOf(',')+2);
		var place=temp.substring(temp.indexOf(',')+2);
		var company=temp.substring(0, temp.indexOf(','));
		var query = docRef.where('section', '==', document.section).where('name', '==', document.speaker).get()
		.then(function(snapshot) {
		if(snapshot.size > 0) {
            console.log('Speaker already in db:' + document.speaker);
		}
		else{
			docRef.add({
					name: document.speaker,
					section: document.section,
					job: company,
					city: place,
					photo: document.img,
					speech: document.name,
					avgRating: 0,
					numRatings: 0

				});
			console.log('Speaker added:' + document.speaker);
		}})
		.catch(err => {
			console.log('Error getting documents', err);
		});
	});
}

exports.calling = functions.https.onCall((data, context) => {
		console.log('Start');
		parse('https://dump-conf.ru/section/35/');
		parse('https://dump-conf.ru/section/36/');
	    parse('https://dump-conf.ru/section/37/');
		parse('https://dump-conf.ru/section/38/');
		parse('https://dump-conf.ru/section/39/');
		parse('https://dump-conf.ru/section/40/');
		parse('https://dump-conf.ru/section/41/');
		parse('https://dump-conf.ru/section/42/');
		parse('https://dump-conf.ru/section/56/');
});