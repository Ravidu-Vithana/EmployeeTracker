import mongoose from "mongoose";

const userSchema = new mongoose.Schema({
    name: { type: String, required: true },
    email: { type: String, required: true },
    firebaseToken: { type: String, required: true },
}, { timestamps: true });

const User = mongoose.model('users', userSchema);

export default User;