import mongoose from "mongoose";

const clockSchema = new mongoose.Schema({
    email: { type: String, required: true },
    clockIn: { type: Date, required: true },
    clockOut: { type: Date, required: false },
}, { timestamps: true });

const Clock = mongoose.model('Clock', clockSchema);

export default Clock;