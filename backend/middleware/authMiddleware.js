import admin from "firebase-admin"
import serviceAccount from "../config/firebase-adminsdk.json" assert { type: 'json' };
import expressAsyncHandler from "express-async-handler";
import User from "../model/userModel.js";

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

const authorizeToken = expressAsyncHandler(async (req, res, next) => {
    try {
        console.log("authorization started");
        const decodedToken = await admin.auth().verifyIdToken(req.body.firebaseToken);
        const email = decodedToken.email;
        req.user = await User.findOne({ email });
        console.log("authorization ended: " + req.user);
        next();
    } catch (error) {
        res.status(401).json({ message: "Invalid token" });
    }
});

export default authorizeToken;