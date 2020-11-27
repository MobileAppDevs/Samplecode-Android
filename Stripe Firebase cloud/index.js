const functions = require('firebase-functions');
const admin = require('firebase-admin');
const serviceAccount = './serviceAccountKey.json';
const path = require('path');
const fs = require('fs');
const os = require('os');
const Busboy = require('busboy');
const stripe = require('stripe')('XXXXX');
const FCM = require('fcm-node');
const nodemailer = require('nodemailer');
const smtpTransport = require('nodemailer-smtp-transport');
//Extra
const rp = require('request-promise');

admin.initializeApp({
	credential: admin.credential.cert(serviceAccount),
	databaseURL: "https://trippiefinaltwo.firebaseio.com/"
});
const PAYMENT_STATUS = ['PAYMENT_ON_HOLD', 'PAYMENT_COLLECTED_BY_STRIPE', 'PAYMENT_REFUNDED', 'PAYMENT_ON_CANCELLED', 'PAYMENT_SETTLED'];

// process.env.FIREBASE_CONFIG=JSON.stringify({
//    projectId: "trippiefinaltwo", // replace with your projectId
//    databaseURL: "https://trippiefinaltwo.firebaseio.com" // replace with yours 
// });
// admin.initializeApp(functions.config().firebase);

exports.createCustomer = functions.https.onRequest(async (request, response) => {
	try {
		const userId = request.body.user_id;
		// const userId = 'FlLgDhyzfHPbcvTJjHrdJmTnugP2';
		const user = await getDataFromFirestore('users', userId);
		console.log("user >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		console.log(user);
		if (user) {
			const customer = await stripe.customers.create({
				email: user.email,
				phone: user.phone,
				description: userId
			});
			console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>", customer);
			await updateDataInFS('users', userId, { customer_id: customer.id });
			response.status(200).send({ msg: "customer created successfully", customer: customer });
		} else {
			response.status(204).send({ msg: "User record not found" });
		}
	} catch (error) {
		console.log("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", error);
		response.status(500).send({ msg: error.toString() });
	}
});

exports.createEphemeralKeys = functions.https.onRequest(async (request, response) => {
	try {
		console.log("request >>>>>>>>>>>>>>>>>>>>>>>>>>>", request.body);
		console.log(request.body)
		const userId = request.body.user_id;
		// const userId = 'FlLgDhyzfHPbcvTJjHrdJmTnugP2';
		const apiVersion = request.body.api_version;
		// const apiVersion = '2019-05-16';
		const user = await getDataFromFirestore('users', userId);
		console.log(user);
		let key = await stripe.ephemeralKeys.create(
			{ customer: user.customer_id },
			{ apiVersion: apiVersion }
		);
		response.status(200).send({ msg: "Ephemeral key created successfully", key: key });
	} catch (error) {
		response.status(500).send({ msg: error.toString() });
	}
});

exports.createIntent = functions.https.onCall(async (data, context) => {
	if (!context.auth) {
		return { message: 'Authentication Required!', code: 401 };
	} else {
		try {
			const paymentType = data.payment_type;
			const currency = data.currency;
			const userId = data.user_id;
			const trippieId = data.trippie_id;
			const user = await getDataFromFirestore('users', userId);
			console.log(data);
			if (user) {
				console.log("paymentType>>>>>>>>>>>>>>>>>", paymentType);
				console.log("paymentType>>>>>>>>>>>>>>>>>", typeof (paymentType));
				if (paymentType === "STRIPE") {
					const paymentIntent = await stripe.paymentIntents.create({
						customer: user.customer_id,
						amount: 100,
						currency: currency
					});

					console.log("paymentIntent>>>>>>>>>>>>>>>>>");
					console.log(paymentIntent);
					const clientSecret = paymentIntent.client_secret;
					const updateData = {
						paymentStatus: PAYMENT_STATUS[3],
						paymentIntentId: paymentIntent.id
					};

					console.log("updateData>>>>>>>>>>>>>>>>>");
					console.log(updateData);
					await updateDataInFS('trippies', trippieId, updateData);
					// response.status(200).send({msg: "Payment Intent created successfully", clientSecret: clientSecret});
					return { msg: "Payment Intent created successfully", code: 200, clientSecret: clientSecret };
				} else if (paymentType === "PAYMENT_ONHOLD") {
					const amount = data.amount;
					const driverAmount = data.driver_amount;
					const paymentIntent = await stripe.paymentIntents.create({
						customer: user.customer_id,
						amount: amount,
						currency: currency,
						transfer_group: `Trippie_${trippieId}`
					});
					console.log("paymentIntent>>>>>>>>>>>>>>>>>");
					console.log(paymentIntent);
					const clientSecret = paymentIntent.client_secret;
					const updateData = {
						driverAmount: driverAmount,
						JobDeliverTime: null,
						paymentStatus: PAYMENT_STATUS[0],
						paymentIntentId: paymentIntent.id
					};

					console.log("updateData>>>>>>>>>>>>>>>>>");
					console.log(updateData);
					await updateDataInFS('trippies', trippieId, updateData);
					// response.status(200).send({msg: "Payment Intent created successfully", clientSecret: clientSecret});
					console.log("Payment Intent created successfully>>>>>>>>>>>>>>>>>");
					return { msg: "Payment Intent created successfully", code: 200, clientSecret: clientSecret };
				} else {
					// response.status(500).send({msg: "Payment Type is not Valid" });
					console.log("Payment Type is not Valid>>>>>>>>>>>>>>>>>");
					return { msg: 'Payment Type is not Valid', code: 500 };
				}
			} else {
				// response.status(204).send({msg: "User record not found" });
				console.log("User record not found>>>>>>>>>>>>>>>>>");
				return { msg: 'User record not found', code: 204 };
			}
		} catch (error) {
			// response.status(500).send({msg: error.toString()});
			console.log(">>>>>>>>>>>>>>>>>", error.toString());
			return { msg: error.toString(), code: 500 };
		}
	}
});

exports.refundPaymentToCustomer = functions.https.onCall(async (data, context) => {
	if (!context.auth) {
		return { msg: 'Authentication Required!', code: 401 };
	} else {
		try {
			const trippieId = data.trippie_id;
			let totalAmount = data.total_amount;
			const amountDeducted = data.amount_tb_deducted;
			const trip = await getDataFromFirestore('trippies', trippieId);
			console.log(data);
			let refund;
			if (amountDeducted === 0 || amountDeducted === 0.00) {
				totalAmount = totalAmount - ((totalAmount * 3.05) / 100);
				refund = await stripe.refunds.create({
					amount: Math.floor(totalAmount),
					payment_intent: trip.paymentIntentId
				});

				const updateData = {
					paymentStatus: PAYMENT_STATUS[2]
				};

				console.log("updateData>>>>>>>>>>>>>>>>>");
				console.log(updateData);
				await updateDataInFS('trippies', trippieId, updateData);
			} else {
				let decPer = 12;
				let refundAmount = totalAmount - ((totalAmount * 3.05) / 100) - amountDeducted;
				if (refundAmount > 0) {
					refund = await stripe.refunds.create({
						amount: Math.floor(refundAmount),
						payment_intent: trip.paymentIntentId,
					});
				} else {
					decPer = 15.05;
				}

				let transferAmount = (amountDeducted - (amountDeducted * decPer) / 100);
				const driver = await getDataFromFirestore('users', trip.driverId);
				console.log("driver >>>>>>>>>>>>>>>>>>>>>>>>>");
				console.log(driver)
				const driverPayment = await stripe.transfers.create({
					amount: Math.floor(transferAmount),
					currency: 'NZD',
					destination: driver.stripe_account_id,
					transfer_group: `Trippie_${trip.trippieId}`
				});
				console.log("driverPayment >>>>>>>>>>>>>>>>>>>>>>>>>");
				console.log(driverPayment)
				const updateData = {
					paymentStatus: PAYMENT_STATUS[4],
					amountDeducted: amountDeducted,
					driverTransferId: driverPayment.id
				};
				await updateDataInFS('trippies', trip.trippieId, updateData);
			}

			// response.status(200).send({msg: "Amount Refunded Successfully", refund});
			console.log("Amount Refunded Successfully >>>>>>>>>>>>>>>>>>>>>>>>>");
			return { msg: 'Amount Refunded Successfully', code: 200, refund };
		} catch (error) {
			// response.status(500).send({msg: error.toString()});
			console.log("Error >>>>>>>>>>>>>>>>>>>>>>>>>", error.toString());
			return { msg: error.toString(), code: 500 };
		}
	}
});

exports.createDriverStripeAccount = functions.https.onRequest(async (request, response) => {
	try {
		const userId = request.body.user_id;
		const country = request.body.country;
		const user = await getDataFromFirestore('users', userId);
		if (user) {
			let data = {
				type: 'custom',
				country: country,
				email: user.email,
				business_type: 'individual',
				requested_capabilities: ['card_payments', 'transfers']
			};
			let userAccount = await stripe.accounts.create(data);
			await updateDataInFS('users', userId, { stripe_account_id: userAccount.id });
			var tosData = {
				tos_acceptance: {
					date: Math.floor(Date.now() / 1000),
					ip: '127.0.0.1'
				}
			};
			await stripe.accounts.update(userAccount.id, tosData);
			response.status(200).send({ msg: "User Account created successfully", userAccount: userAccount });
		} else {
			response.status(204).send({ msg: "User record not found" });
		}
	} catch (error) {
		response.status(500).send({ msg: error.toString() });
	}
});

exports.getDriverStripeAccount = functions.https.onRequest(async (request, response) => {
	try {
		const userId = request.body.user_id;
		const user = await getDataFromFirestore('users', userId);
		if (user && user.stripe_account_id) {
			let userAccount = await stripe.accounts.retrieve(user.stripe_account_id);
			response.status(200).send({ msg: "User Account created successfully", userAccount: userAccount });
		} else {
			response.status(204).send({ msg: "User record not found" });
		}
	} catch (error) {
		response.status(500).send({ msg: error.toString() });
	}
});

exports.updateDriverStripeAccount = functions.https.onCall(async (data, context) => {
	try {
		if (!context.auth) {
			return { message: 'Authentication Required!', code: 401 };
		} else {
			const userId = data.user_id;
			// const userId = 'FlLgDhyzfHPbcvTJjHrdJmTnugP2';
			const user = await getDataFromFirestore('users', userId);
			const source = data.source;
			const userData = await stripe.accounts.update(user.stripe_account_id, source);
			return { msg: "User Account updated successfully", success: true, code: 200, userAccount: userData };
		}
	} catch (error) {
		throw new Error(error.toString());
	}
});

exports.updateDriverIdentitiyProof = functions.https.onRequest(async (request, response) => {
	try {
		// const userId = 'kGzGifrAzcawyxdnYanyeAu6VQx2';
		const uploadedfile = await uploadFile(request);
		const userId = uploadedfile.fields.user_id;
		const user = await getDataFromFirestore('users', userId);
		const imagePromise = [];
		Object.keys(uploadedfile.uploads).forEach((key, index) => {
			imagePromise.push(uploadImageToStripe(uploadedfile.uploads[key], key, user));
		})
		const dataResponse = await Promise.all(imagePromise);
		for (const key of Object.keys(uploadedfile.uploads)) {
			fs.unlinkSync(uploadedfile.uploads[key]);
		}
		response.status(200).send({ msg: "Account Updated successfully", dataResponse });
	} catch (error) {
		response.status(500).send({ msg: error.toString() });
	}
});

var uploadFile = async (req) => {
	return new Promise(async (resolve, reject) => {
		const busboy = new Busboy({ headers: req.headers });
		const tmpdir = os.tmpdir();
		const fields = {};
		const uploads = {};
		busboy.on('field', (fieldname, val) => {
			fields[fieldname] = val;
		});

		const fileWrites = [];

		// This code will process each file uploaded.
		busboy.on('file', (fieldname, file, filename) => {
			const filepath = path.join(tmpdir, filename);
			uploads[fieldname] = filepath;

			const writeStream = fs.createWriteStream(filepath);
			file.pipe(writeStream);

			// File was processed by Busboy; wait for it to be written to disk.
			const promise = new Promise((res, rej) => {
				file.on('end', () => {
					writeStream.end();
				});
				writeStream.on('finish', res);
				writeStream.on('error', rej);
			});
			fileWrites.push(promise);
		});
		busboy.on('finish', async () => {
			await Promise.all(fileWrites);
			resolve({ fields: fields, uploads: uploads });
		});

		busboy.end(req.rawBody);
	});
};

var uploadImageToStripe = async (filePath, fieldName, user) => {
	return new Promise(async (resolve, reject) => {
		try {
			const fileName = filePath.split("/tmp/")[1];
			const uploadedFile = await stripe.files.create({
				purpose: 'identity_document',
				file: {
					data: fs.readFileSync(filePath),
					name: fileName,
					type: 'application/octet-stream',
				},
			}, {
				stripeAccount: user.stripe_account_id,
			});
			var dataObj = {};
			switch (fieldName) {
				case "document_front":
					dataObj = { document: { front: uploadedFile.id } };
					break;
				case "document_back":
					dataObj = { document: { back: uploadedFile.id } };
					break;
				case "additional_document_front":
					dataObj = { additional_document: { front: uploadedFile.id } };
					break;
				case "additional_document_back":
					dataObj = { additional_document: { back: uploadedFile.id } };
					break;
			}
			const account = await stripe.accounts.update(user.stripe_account_id, { individual: { verification: dataObj } });
			resolve(account);
		} catch (error) {
			reject(error);
		}
	});
};

exports.getStripeCustomerDetails = functions.https.onCall(async (data, context) => {
	try {
		if (!context.auth) {
			return { message: 'Authentication Required!', code: 401 };
		} else {
			const userId = data.user_id;
			const user = await getDataFromFirestore('users', userId);
			if (user && user.customer_id) {
				let customer = await getStripeCustomerDetails(user.customer_id);
				return { msg: "Customer Detail fetch successfully", code: 200, customer: customer };
			} else {
				return { msg: "User record not found", code: 204 };
			}
		}
	} catch (error) {
		return { msg: error.toString(), code: 500 };
	}
});

exports.getStripeLink = functions.https.onRequest(async (request, response) => {
	try {
		// const userId = request.body.user_id;
		const userStripeId = 'acct_1GIuFTDrOw33IJyV';
		let link = await stripe.accounts.createLoginLink(userStripeId);
		response.status(200).send({ link: link });
		// const user = await getDataFromFirestore('users', userId);
		// if(user && user.customer_id) {
		// 	let customer = await getStripeCustomerDetails(user.customer_id);
		// 	response.status(200).send({msg: "Customer Detail fetch successfully", customer: customer });
		// } else {
		// 	response.status(204).send({msg: "User record not found" });
		// }
	} catch (error) {
		response.status(500).send({ msg: error.toString() });
	}
});

var getStripeCustomerDetails = async (customer_id) => {
	let customer = await stripe.customers.retrieve(customer_id);
	return customer;
};

var getDataFromFirestore = async (collectionName, docId) => {
	return admin.firestore().doc(collectionName + "/" + docId).get().then((snapShot) => {
		if (snapShot.exists) {
			let data = snapShot.data();
			return data;
		} else {
			throw new Error('No user Found.');
		}
	}).catch((error) => {
		throw error;
	});
}

var updateDataInFS = async (collectionName, docId, updateRecord) => {
	return admin.firestore().doc(collectionName + "/" + docId).update(updateRecord).then((snapShot) => {
		return true;
	}).catch((err) => {
		console.log('err>>>>', err);
		throw err;
	});
}

exports.sendUserRegistrationEmail = functions.firestore.document('users/{id}').onCreate(async (snap, context) => {
	const user = snap.data();
	const templete = `<p>Hello ${user.firstNm} ${user.lastNm},</p>
						<p>Thank you for registering and welcome to the Trippie community. You can now post Trippies. This means you are now ready to enjoy the savings and convenience of shared economy delivery. Our community is very important to us. If you have any questions concerns or feedback, please don’t hesitate to contact our friendly team.
						If you want to register as a Tripster which will allow you to complete Trippies and turn your extra space into extra cash simply navigate to your user tab and complete the simple ‘Driver Registration’ process. For more information about the registration process and everything else related to Trippie please go to https://trippie.co.nz/.
						</p>
						</br>
						<p>Happy trails, the Trippie Team.</p>`;
	let mailOptions = {
		from: 'info@trippie.co.nz',
		to: user.email,
		subject: 'User Register',
		html: templete
	};
	return sendEmail(mailOptions);
});

exports.sendDriverRegistrationEmail = functions.firestore.document('drivers/{id}').onCreate(async (snap, context) => {
	const driver = snap.data();
	const user = await getDataFromFirestore('users', driver.driverId);
	const templete = `<p>Hello ${user.firstNm} ${user.lastNm},</p>
						<p>Thank you for completing the driver registration process. You are now registered as a Tripster which means you can make offers, complete Trippies and turn your extra space into extra cash. For more about how to make offers and complete Trippies please go to our website https://trippie.co.nz/. Our community is very important to us. If you have any questions, concerns or feedback, please don’t hesitate to contact our friendly team. Please take some time to get to know our Terms and Conditions. Stay safe and respect your fellow community members. You are currently able to complete item Trippies. Please go to your settings tab and complete the additional forms to become a pet tripster and/or a livestock tripster.</p></br> 
						<p>Happy trails, the Trippie Team.</p>`;
	let mailOptions = {
		from: 'info@trippie.co.nz',
		to: user.email,
		bcc: ['thomas@trippie.co.nz', { name: 'Thomas Bailey', address: 'thomas@trippie.co.nz' }],
		subject: 'Driver Register',
		html: templete
	};
	return sendEmail(mailOptions);
});

function sendEmail(mailOptions) {
	let transporter = nodemailer.createTransport(smtpTransport({
		"host": 'email-smtp.ap-southeast-2.amazonaws.com',
		"port": 465,
		"secure": true,
		"auth": {
			"user": 'AKIAXBWGCE6FHDEDPFGC',
			"pass": 'BGzA+Ltb8ZqzNRPdUED0BbgssYzNrctP3ecip1jdoyk8'
		},
		tls: {
			rejectUnauthorized: false
		}
	}));

	transporter.sendMail(mailOptions, function (err, info) {
		if (err) {
			console.log(err)
			return false
		} else {
			return true;
		}
	});
}

exports.sendAllNotification = functions.https.onCall(async (data, context) => {
	if (!context.auth) {
		return { message: 'Authentication Required!', code: 401 };
	} else {
		try {
			// let data = data1.body;
			if (data && data.receiverUserId) {
				let dbCustomer = await getDataFromFirestoreDetails('users/' + data.receiverUserId);
				if (dbCustomer && dbCustomer.token) {
					let notification = await fcmNotifications(data, dbCustomer.token);
					if (notification) {
						return { msg: "Notification Send Successfully", code: 200 };
					} else {
						return { msg: "Something has gone wrong!", code: 404 };
					}
				} else {
					return { msg: "User not Found", code: 204 };
				}
			} else {
				return { msg: "Please send receiver Id", code: 400 };
			}
		} catch (error) {
			return { msg: error.toString(), code: 500 };
		}
	}
});

async function fcmNotifications(data, receiverFcmToken) {
	var message = {
		// to: receiverFcmToken,  // Multiple tokens in an array
		notification: {
			title: data.title,
			body: data.message
		},
		data: {
			message: data.message,
			notificationType: data.notificationType,
			platform: data.platform,
			receiverUserId: data.receiverUserId,
			senderUserId: data.senderUserId,
			title: data.title,
			trippieId: data.trippieId
		}
	};
	return admin.messaging().sendToDevice(receiverFcmToken, message).then(response => {
		return true;
	}).catch(function (error) {
		console.log("Error sending message:", error);
		throw error;
	});
}

exports.sendNotification = functions.https.onCall(async (data, context) => {
	if (!context.auth) {
		return { message: 'Authentication Required!', code: 401 };
	} else {
		if (data && data.receiverUserId && (typeof (data.receiverUserId) === 'string')) {
			let dbCustomer = await getDataFromFirestoreDetails('users/' + data.receiverUserId);
			if (dbCustomer && dbCustomer.token) {
				let notification = await updateNotificationCollection('notifications/', dbCustomer.token, data, dbCustomer.image, data.receiverUserId);
				return { msg: "Notification Send Successfully", code: 200 };
			} else {
				return { msg: "User not Found", code: 204 };
			}
		} else if (data && data.receiverUserId && (typeof (data.receiverUserId) === 'object')) {
			let userPromise = [];
			data.receiverUserId.forEach((userId) => {
				userPromise.push(getDataFromFirestoreDetails('users/' + userId));
			})
			let users = await Promise.all(userPromise);
			let updateNotificationPromise = [];
			users.forEach((user) => {
				if (user && user.token) {
					updateNotificationPromise.push(updateNotificationCollection('notifications/', user.token, data, user.image, user.userId));
				}
			})
			let pdata = await Promise.all(updateNotificationPromise);
			return { msg: "Notification Send Successfully", code: 200 };
		} else {
			return { msg: "Please send receiver Id", code: 400 };
		}
	}
});


function getDataFromFirestoreDetails(path) {
	return admin.firestore().doc(path).get().then(documentSnapshot => {
		if (documentSnapshot.exists) {
			let data = documentSnapshot.data();
			return data;
		} else {
			throw new Error("No Data Found");
		}
	}).catch((error) => {
		throw error;
	});
}


function updateNotificationCollection(path, receiverFcmToken, data, image, receiverUserId) {
	return admin.firestore().collection('notifications').doc().set({
		createdTime: new Date(),
		message: data.message,
		notificationType: data.notificationType,
		platform: data.platform,
		receiverUserId: receiverUserId,
		senderUserId: data.senderUserId,
		title: data.title,
		trippieId: data.trippieId,
		receiverFcmToken: receiverFcmToken,
		senderFcmToken: data.senderFcmToken,
		receiverUserImageURL: image,
		senderUserImageURL: data.senderUserImageURL
	}).then(data => {
		return true;
	}).catch((error) => {
		console.log("errorr>>>>>>>>>>>>>>>>>>>", error);
		throw error;
	});
}

exports.fireNotification = functions.firestore.document('notifications/{id}').onWrite((change, context) => {
	const notificationData = change.after.data();
	const senderUserId = notificationData.senderUserId;
	const receiverUserId = notificationData.receiverUserId;
	const title = notificationData.title;
	const notificationType = notificationData.notificationType;
	const platform = notificationData.platform;
	const createdTime = notificationData.createdTime;
	const trippieId = notificationData.trippieId;
	const message = notificationData.message;
	const receiverFcmToken = notificationData.receiverFcmToken;
	// const receiverFcmToken = 'cUYwA5PL4WE:APA91bGulGeuLVL7TSjRfXSCMEtaJ3lTtB9Xy7jwZqU_U81hP3SRLe4-wYPB2kvW8RiVs9C-KiDiytSc3Fox1QnmwQUMYpJnjbywOmsXiJPNr0HaNZ1kgmzVX3UHGUOmxnEosFuXg7zm'
	var FCM = require('fcm-node');
	var serverKey = 'sk_test_n60olovYEBZxRmuvTiz2B1l400EewyOg9p'; //put your server key here
	var fcm = new FCM(serverKey);
	var payload = { //this may vary according to the message type (single recipient, multicast, topic, et cetera)
		notification: {
			title: title,
			body: message,
			sound: 'default',
		},
		data: {  //you can send only notification or only data(or include both)
			platform: platform,
			trippieId: trippieId,
			senderUserId: senderUserId,
			receiverUserId: receiverUserId,
			notificationType: notificationType,
			// createdTime: createdTime,
			// receiverFcmToken: receiverFcmToken
		}
	};

	return admin.messaging().sendToDevice(receiverFcmToken, payload).then(response => {
		return true;
	}).catch(function (error) {
		console.log("Error sending message:", error);
		throw error;
	});
});


exports.transferPaymentToDriver = functions.pubsub.schedule('* * * * *').onRun(async (context) => {
	try {
		const trippies = await getRangeTrippies();
		if (trippies && trippies.length > 0) {
			let trippiePromise = [];
			const timestamp = new Date().valueOf();
			console.log("trippies >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			console.log(trippies);
			trippies.forEach((trip) => {
				// const rangeStartTimestamp = trip.JobDeliverTime + (24 * 60 * 60 * 1000);
				const rangeStartTimestamp = trip.JobDeliverTime + (1 * 60 * 1000);
				const rangeEndTimestamp = rangeStartTimestamp + (5 * 60 * 1000);
				if ((timestamp > rangeStartTimestamp) && (timestamp <= rangeEndTimestamp)) {
					trippiePromise.push(transferPaymentToDriver(trip));
				}
			})
			await Promise.all(trippiePromise);
		} else {
			console.log("No Trippie Found for payment ....");
		}
	} catch (error) {
		console.log(error);
		throw error;
	}
});

async function transferPaymentToDriver(trip) {
	return new Promise(async (resolve, reject) => {
		try {
			const driver = await getDataFromFirestore('users', trip.driverId);
			console.log("driver >>>>>>>>>>>>>>>>>>>>>>>>>");
			console.log(driver)
			const driverAmount = trip.driverAmount - ((trip.driverAmount * 3.05) / 100) - ((trip.driverAmount * 12) / 100);
			const driverPayment = await stripe.transfers.create({
				amount: Math.floor(driverAmount),
				currency: 'NZD',
				destination: driver.stripe_account_id,
				transfer_group: `Trippie_${trip.trippieId}`
			});
			console.log("driverPayment >>>>>>>>>>>>>>>>>>>>>>>>>");
			console.log(driverPayment)
			const updateData = {
				paymentStatus: PAYMENT_STATUS[4],
				driverTransferId: driverPayment.id
			};

			await updateDataInFS('trippies', trip.trippieId, updateData);
			resolve();
		} catch (error) {
			reject(error);
		}
	})
}

function getRangeTrippies() {
	return new Promise((resolve, reject) => {
		return admin.firestore().collection('trippies').where('paymentStatus', '==', PAYMENT_STATUS[1]).get().then(snapshot => {
			if (snapshot.empty) {
				return resolve([]);
			} else {
				let data = [];
				snapshot.forEach(doc => {
					data.push(doc.data());
				});
				return resolve(data);
			}
		}).catch(err => {
			console.log('Error getting documents', err);
			return reject(err);
		});
	});
}


exports.updateDriverCancelCount = functions.https.onRequest(async (request, response) => {
	try {
		const userId = request.body.user_id;
		const trippieId = request.body.trippie_id;
		const description = request.body.description;
		const pickTime = request.body.pick_time;
		const cancellationTime = request.body.cancellation_time;
		const user = await getDataFromFirestore('users', userId);
		if (user) {
			let obj = {}
			if ((pickTime - cancellationTime) > (60 * 60 * 1000)) {
				let count = user.cancellationCount ? (user.cancellationCount + 1) : 1;
				obj['cancellationCount'] = count;
				if (obj['cancellationCount'] > 6) {
					obj['ableToMakeOffers'] = false;
				}
			} else if (((pickTime - cancellationTime) > (15 * 60 * 1000)) && ((pickTime - cancellationTime) <= (60 * 60 * 1000))) {
				let count = user.lateCancellationCount ? (user.lateCancellationCount + 1) : 1;
				obj['lateCancellationCount'] = count;
				if (obj['lateCancellationCount'] > 4) {
					obj['ableToMakeOffers'] = false;
				}
			} else if ((pickTime - cancellationTime) < (15 * 60 * 1000)) {
				let count = user.noShowCount ? (user.noShowCount + 1) : 1;
				obj['noShowCount'] = count;
				if (obj['noShowCount'] > 3) {
					obj['ableToMakeOffers'] = false;
				}
			}

			await updateDataInFS('users', userId, obj);
			await updateDataInFS('trippies', trippieId, { jobCancelDescription: description });
			response.status(200).send({ msg: "Driver Account updated successfully" });
		} else {
			response.status(400).send({ msg: "Driver not Found.." });
		}
	} catch (error) {
		response.status(500).send({ msg: error.toString() });
	}
});

exports.trackDriverCancelCount = functions.pubsub.schedule('1 0 * * *').onRun(async (context) => {
	try {
		let drivers = await getDrivers();
		if (drivers && drivers.length > 0) {
			let driverPromise = [];
			drivers.forEach((driver) => {
				driverPromise.push(manageDrivers(driver));
			})
			await Promise.all(driverPromise);
		} else {
			console.log("No driver Found ...");
		}
	} catch (error) {
		console.log(error);
		throw error;
	}
});

async function manageDrivers(user) {
	return new Promise(async (resolve, reject) => {
		try {
			let userId = user.userId;
			const driver = await getDataFromFirestore('drivers', userId);
			let date = user.driverIntializationDate ? user.driverIntializationDate : driver.creationDate;
			let epocDate = new Date(date).valueOf();
			let todayDate = new Date().valueOf();
			if ((todayDate - epocDate) > 31536000000) {
				let updateObj = {
					noShowCount: 0,
					cancellationCount: 0,
					lateCancellationCount: 0,
					ableToMakeOffers: true,
					driverIntializationDate: new Date()
				}
				await updateDataInFS('users', userId, updateObj);
			} else {
				let obj = {}
				if ((obj['cancellationCount'] > 6) || (obj['lateCancellationCount'] > 4) || (obj['noShowCount'] > 3)) {
					if (user.ableToMakeOffers && user.ableToMakeOffers === true) {
						await updateDataInFS('users', userId, { ableToMakeOffers: false });
					}
				}
			}
			resolve();
		} catch (err) {
			reject(err);
		}
	})
}

function getDrivers() {
	return new Promise((resolve, reject) => {
		return admin.firestore().collection('users').where('driver', '==', true).get().then(snapshot => {
			if (snapshot.empty) {
				return resolve([]);
			} else {
				let data = [];
				snapshot.forEach(doc => {
					data.push(doc.data());
				});
				return resolve(data);
			}
		}).catch(err => {
			console.log('Error getting documents', err);
			return reject(err);
		});
	});
}

exports.createNewTrippeId = functions.firestore.document('trippies/{id}').onCreate(async (snap, context) => {
	const date = new Date();
	const trippeId = generateTrippeId(date);
	await updateDataInFS('trippies', context.params.id, { trippieRef: trippeId });
});

function generateTrippeId(d) {
	let month = d.getMonth();
	let day = d.getDate();
	let year = d.getFullYear();
	const currentHours = ('0' + d.getHours()).toString().substr(-2);
	const min = ('0' + d.getMinutes()).toString().substr(-2);
	const sec = ('0' + d.getSeconds()).toString().substr(-2);
	const msec = ('0' + d.getMilliseconds()).toString().substr(-2);
	let random_num = Math.floor(Math.random() * 100);
	year = year.toString().substr(2, 2);
	month = month + 1;
	month = month + "";
	if (month.length === 1) {
		month = "0" + month;
	}
	day = day + "";
	if (day.length === 1) {
		day = "0" + day;
	}
	if (random_num.length === 1) {
		random_num = "0" + random_num;
	}
	return year + month + day + currentHours + min + sec + msec + random_num;
}

exports.validateIncidentReport = functions.https.onRequest(async (request, response) => {
	const data = request.body;
	const trippies = await checkTrippiesRefExist(data);
	if (trippies && trippies.length > 0) {
		response.status(200).send({ status: true, msg: "valid!" });
	} else {
		response.status(200).send({ status: false, msg: "This Trippe id does not exist, please enter correct id!" });
	}
});

exports.createIncidentReport = functions.https.onRequest(async (request, response) => {
	const data = request.body;
	const trippies = await checkTrippiesRefExist(data);
	if (trippies && trippies.length > 0) {
		await saveIncidentReport(data)
		response.status(200).send({ status: true, msg: "Data has been saved successfully!" });
	} else {
		response.status(200).send({ status: false, msg: "This Trippe id does not exist, please enter correct id!" });
	}
});

function checkTrippiesRefExist(data) {
	return new Promise((resolve, reject) => {
		return admin.firestore().collection('trippies').where('trippieRef', '==', data.trippe_id).get().then(snapshot => {
			if (snapshot.empty) {
				return resolve([]);
			} else {
				let data = [];
				snapshot.forEach(doc => {
					data.push(doc.data());
				});
				return resolve(data);
			}
		}).catch(err => {
			console.log('Error getting documents', err);
			return reject(err);
		});
	});
}

async function saveIncidentReport(data) {
	return admin.firestore().collection('incidentReports').doc().set({
		firstName: data.first_name,
		lastName: data.last_name,
		trippieRef: data.trippe_id,
		phone: data.phone,
		typeOfIncident: data.type_of_incident,
		description: data.description
	}).then(data => {
		return true;
	}).catch((error) => {
		console.log(error);
		throw error;
	});
}


/**Append extra funtion that is already exist in firebae funtion**/
exports.auth = functions.https.onCall(async (data, context) => {

	var makeInactive = data.makeInactive;
	var trippieRef = admin.firestore().collection('trippies').doc(data.trippieId);

	trippieRef.get().then(doc => {
		if (!doc.exists) {
			var errorMsg = 'No such Trippie exists!';
			throw new Error(errorMsg);
		} else {
			if (doc.data().userId === context.auth.uid) {
				if (makeInactive) {
					admin.firestore().collection('trippies').doc(data.trippieId).update({
						active: false
					});
				}
				return admin.firestore().collection('lists').doc('trippiesList').update({
					deletedTrippies: admin.firestore.FieldValue.arrayUnion(data.trippieId),
					activeTrippies: admin.firestore.FieldValue.arrayRemove(data.trippieId)
				});
			} else {
				throw new Error('No such Trippie user found!');
			}
		}
	}).catch(error => {
		console.log('No such Trippie exists!', error);
		throw error;
	});
});

exports.ratings = functions.https.onCall(async (data, context) => {
	const isDriver = data.isDriver;
	const inputRating = data.inputRating;
	var collection = 'users';
	const userToRateId = data.userToRateId;
	const selectedComplaint = data.selectedComplaint;
	const trippieId = data.trippieId;

	if (isDriver) {
		collection = 'drivers';
	}

	var userToRateRef = admin.firestore().collection(collection).doc(userToRateId);
	userToRateRef.get().then(doc => {
		if (!doc.exists) {
			var errorMsg = 'No such user exists!';
			throw new Error(errorMsg);
		} else {
			var rating = doc.data().rating;
			var numberOfRatings = doc.data().numberOfRatings;

			var totalRating = rating * numberOfRatings;

			var randomBounds = null;


			if (isDriver && rating < 5) {
				switch (selectedComplaint) {
					case 1: randomBounds = [30, 40]; break;
					case 2: randomBounds = [35, 50]; break;
					case 3: randomBounds = [75, 100]; break;
					case 4: process.exit(1); break;
				}
			}

			var adjustedRating = inputRating;
			if (randomBounds !== null) {
				var weighting = Math.random() * (randomBounds[1] - randomBounds[0] + 1) + randomBounds[0];
				adjustedRating += (5 - inputRating) * (100 - weighting) / 100;
			}

			if (numberOfRatings > 0) {
				totalRating += adjustedRating;
				rating = totalRating / ++numberOfRatings;
			}
			else {
				rating = adjustedRating;
				numberOfRatings++;
			}

			var updateBoolean = { driverHasBeenRated: true };

			if (!isDriver) {
				updateBoolean = { userHasBeenRated: true };
			}

			admin.firestore().collection('trippies').doc(trippieId).update(updateBoolean);

			return admin.firestore().collection(collection).doc(userToRateId).update({
				rating: rating,
				numberOfRatings: numberOfRatings
			});

		}
	}).catch(err => {
		return reject(err);
	});
});

exports.scheduledExpiryFunction = functions.pubsub.schedule('every 5 minutes').timeZone('Pacific/Auckland').onRun((context) => {
	const today = new Date();
	admin.firestore().collection('lists').doc('trippiesList').get().then(trippiesList => {
		const activeTrippies = trippiesList.data().activeTrippies;
		var expiredTrippieIds = [];
		var trippiesProcessed = 0;
		var result = -1
		console.log("==trippiesProcessed start value==", trippiesProcessed)
		activeTrippies.forEach(function (id) {
			admin.firestore().collection('trippies').where('expiryTime', "<=", today).where('trippieId', "==", id).get().then(expiredTrippie => {
				trippiesProcessed++;
				if (!expiredTrippie.empty) {

					expiredTrippieIds.push(id);
					if (trippiesProcessed === activeTrippies.length) {
						expiredTrippieIds.forEach(expiredTrippie => {
							console.log("Cleaning up " + expiredTrippie);
						});

						admin.firestore().collection('lists').doc('trippiesList').update({
							deletedTrippies: admin.firestore.FieldValue.arrayUnion.apply(this, expiredTrippieIds),
							activeTrippies: admin.firestore.FieldValue.arrayRemove.apply(this, expiredTrippieIds)
						});
						result = 1
						return { result: 1 };
					}
					else {
						return result
					}
				}
				else if (trippiesProcessed === activeTrippies.length) {
					result = 0
					return { result: 0 };
				}
				else {
					return result
				}
			}).catch(err => {
				throw err;
			});
		});
		return result;
	}).catch(err => {
		return reject(err);
	});
});

exports.drivercancellation = functions.https.onCall(async (data, context) => {
	const trippieId = data.trippieId;
	var type = ""; // type of cancellation

	var trippieRef = admin.firestore().collection('trippies').doc(trippieId);
	return trippieRef.get().then(trippie => {

		const pickupTime = trippie.data().pickupTime;

		if (!trippie.exists) {
			var errorMsg = 'No such Trippie exists!';
			throw new Error(errorMsg);
		} else {

			if (cancellerId === context.auth.uid) {

				var currentTimeOne = new Date();
				currentTimeOne.setMinutes(currentTimeOne.getMinutes() + 15);
				const fifteenBeforeCheck = new Date(currentTimeOne);
				var currentTimeTwo = new Date();
				currentTimeTwo.setMinutes(currentTimeTwo.getMinutes() + 60);
				const hourBeforeCheck = new Date(currentTimeTwo);

				if (pickupTime >= hourBeforeCheck) {
					type = "explained";
				}
				else if (pickupTime < hourBeforeCheck && pickupTime >= fifteenBeforeCheck) {
					type = "late";
				}
				else {
					type = "noshow";
				}

				console.log(type);

				admin.firestore().collection('trippies').doc(trippieId).update({
					cancelled: true
				});

				return {
					type: type
				};
			} else {
				throw new Error('No such Trippie user found!');
			}
		}
	}).catch(err => {
		console.log('Error getting documents', err);
		return reject(err);
	});
});

exports.showcancellationfee = functions.https.onCall(async (data, context) => {
	const trippieId = data.trippieId;
	const isDriver = data.isDriver;
	var punishment; // contains fee punishment for user

	var trippieRef = admin.firestore().collection('trippies').doc(trippieId);
	return trippieRef.get().then(trippie => {

		const price = trippie.data().price;
		const pickupTime = trippie.data().pickupTime;

		if (!trippie.exists) {
			var errorMsg = 'No such Trippie exists!';
			throw new Error(errorMsg);
		} else {
			var cancellerId = isDriver ? trippie.data().driverId : trippie.data().userId;
			if (cancellerId === context.auth.uid) {
				if (!isDriver) {
					const postedDate = trippie.data().postedDate && trippie.data().postedDate.toDate() && trippie.data().postedDate.toDate().getTime();
					var currentTime = new Date();
					currentTime.setMinutes(currentTime.getMinutes() - 30); //check 
					const halfHourBefore = new Date(currentTime);
					currentTime.setMinutes(currentTime.getMinutes() + 60);
					const halfHourAfter = new Date(currentTime);

					// if (postedDate >= halfHourBefore){ //if Trippie was posted in the last 30 minutes
					const offersMap = trippie.data().offers;
					if (offersMap === undefined || offersMap === null || !offersMap) {
						punishment = 0;
					}
					else {
						if (trippie.data().driverId === undefined || trippie.data().driverId === null || !trippie.data().driverId) { //check if user has not accepted any offers
							punishment = 1;
						}
						else {
							if (trippie.data().status === "customer_accepted") { //if driver is not yet on the way

								if (halfHourAfter <= pickupTime) { //if pick up time is half an hour or more away
									punishment = price < 10 ? 3 : 5;
								}
								else {
									punishment = price < 10 ? price * 0.5 : 10;
								}
							}
							else { //if driver is on the way
								if (pickupTime > new Date()) { //if it is past the pick up time
									const punishmentCalculation = 0.75 * price + 5;
									punishment = punishmentCalculation < price ? punishmentCalculation : price;
								}
								else {
									const punishmentCalculation = 0.5 * price + 5;
									punishment = punishmentCalculation < price ? punishmentCalculation : price;
								}
							}
						}
					}
				}
				else {
					//check
				}

				console.log(punishment);

				return {
					punishment: punishment
				};
			}
			return {
				punishment: punishment
			};
		}
	}).catch(err => {
		console.log('Error getting documents', err);
		return reject(err);
	});
});

exports.cancellation = functions.https.onCall(async (data, context) => {
	const trippieId = data.trippieId;
	const isDriver = data.isDriver;
	var punishment; // contains fee punishment for user

	var trippieRef = admin.firestore().collection('trippies').doc(trippieId);
	return trippieRef.get().then(trippie => {

		const price = trippie.data().price;
		const pickupTime = trippie.data().pickupTime;

		if (!trippie.exists) {
			var errorMsg = 'No such Trippie exists!';
			throw new Error(errorMsg);
		} else {
			var cancellerId = isDriver ? trippie.data().driverId : trippie.data().userId;
			if (cancellerId === context.auth.uid) {
				if (!isDriver) {
					const offersMap = trippie.data().offers;
					if (offersMap === undefined || offersMap === null || !offersMap) {//THIS CHECK APPEARS TO BE NO LONGER WORKING CORRECTLY
						punishment = 0;
					}
					else {
						if (trippie.data().driverId === undefined || trippie.data().driverId === null || !trippie.data().driverId) { //check if user has not accepted any offers THIS CHECK APPEARS TO BE NO LONGER WORKING CORRECTLY
							punishment = 1;
						}
						else {
							if (trippie.data().status === "customer_accepted") { //if driver is not yet on the way
								punishment = price < 10 ? price * 0.5 : 10;
							}
							else { //if driver is on the way
								const punishmentCalculation = 0.5 * price + 5;
								punishment = punishmentCalculation < price ? punishmentCalculation : price;
							}
						}
					}
				}
				else {
					//check
				}

				console.log(punishment);
				admin.firestore().collection('trippies').doc(trippieId).update({
					cancelled: true
				});
				if (trippie.driverId === undefined || trippie.driverId === null || !trippie.driverId) {//THIS CHECK APPEARS TO BE NO LONGER WORKING CORRECTLY
					admin.firestore().collection('lists').doc('trippiesList').update({
						deletedTrippies: admin.firestore.FieldValue.arrayUnion(trippieId),
						activeTrippies: admin.firestore.FieldValue.arrayRemove(trippieId)
					});
				}

				return {
					punishment: punishment
				};
			}
		}
		return {
			punishment: punishment
		};
	}).catch(err => {
		console.log('Error getting documents', err);
		return reject(err);
	});
});

exports.getCancellationFeeForMessage = functions.https.onCall(async (data, context) => {
	const trippieId = data.trippieId;
	const isDriver = data.isDriver;
	var punishment; // contains fee punishment for user

	var trippieRef = admin.firestore().collection('trippies').doc(trippieId);
	return trippieRef.get().then(trippie => {

		const price = trippie.data().price;
		const pickupTime = trippie.data().pickupTime;
		if (!trippie.exists) {
			var errorMsg = 'No such Trippie exists!';
			throw new Error(errorMsg);
		} else {
			var cancellerId = isDriver ? trippie.data().driverId : trippie.data().userId;
			if (cancellerId === context.auth.uid) {
				if (!isDriver) {

					const offersMap = trippie.data().offers;
					if (offersMap !== null) {//THIS CHECK APPEARS TO BE NO LONGER WORKING CORRECTLY
						if (trippie.data().driverId === null) { //check if user has not accepted any offers//THIS CHECK APPEARS TO BE NO LONGER WORKING CORRECTLY
							punishment = 1;
						}
						else {
							if (trippie.data().status !== undefined || trippie.data().status !== null) {
								if (trippie.data().status === "customer_accepted") { //if driver is not yet on the way

									punishment = price < 10 ? price * 0.5 : 10;
								}
							}
							else { //if driver is on the way

								const punishmentCalculation = 0.5 * price + 5;
								punishment = punishmentCalculation < price ? punishmentCalculation : price;

							}
						}
					}
					else {
						punishment = 0;
					}
				}
				else {
					//check
				}

				console.log("punishment: ", punishment);

				return {
					punishment: punishment
				};

			} else {
				throw new Error('No such Trippie user found!');
			}
		}
	}).catch(err => {
		console.log('Error getting documents', err);
		return reject(err);
	});
});

exports.addToActiveList = functions.firestore.document('/trippies/{id}').onCreate(async (snapshot, context) => {
	const id = context.params.id;
	console.log('debug', id);
	return admin.firestore().collection("lists").doc("trippiesList").update({
		activeTrippies: admin.firestore.FieldValue.arrayUnion(id)
	});
});