import express from 'express';
import Clock from '../model/clockModel.js';

const Router = express.Router();

Router.post('/', async (req, res) => {

    const email = req.body.email;

    try {
        const startOfToday = new Date();
        startOfToday.setHours(0, 0, 0, 0); // Set to the start of today

        const endOfToday = new Date();
        endOfToday.setHours(23, 59, 59, 999); // Set to the end of today

        // Find a document for today
        const existingEntry = await Clock.findOne({
            email,
            clockIn: { $gte: startOfToday, $lte: endOfToday },
        });

        if (!existingEntry) {  //create new clock with clock in entry
            const newClock = new Clock({
                email,
                clockIn: new Date(),
                clockOut: null,
            });
            await newClock.save();
            console.log("New clock-in created:", newClock);
            res.status(201).json({ message: "Clock In Successful" });

        } else if (!existingEntry.clockOut) { //update clock with clockout entry

            existingEntry.clockOut = new Date();
            await existingEntry.save();
            console.log("Clock-out updated:", existingEntry);
            res.status(201).json({ message: "Clock Out Successful" });
        } else {

            console.log("no clock needed");

            console.log("User already clocked in and out for today.");
            res.status(200).json({ message: "You already clocked out for today!" });
        }
    } catch (error) {
        console.error("Error handling clock entry:", error);
        res.status(500).json({ message: "error" });
    }


});

Router.get('/:email', async (req, res) => {

    const email = req.params.email;
    console.log("received");
    try {
        const startOfToday = new Date();
        startOfToday.setHours(0, 0, 0, 0); // Set to the start of today

        const endOfToday = new Date();
        endOfToday.setHours(23, 59, 59, 999); // Set to the end of today

        // Find a document for today
        const existingEntry = await Clock.findOne({
            email,
            clockIn: { $gte: startOfToday, $lte: endOfToday },
        });

        if (!existingEntry) {
            console.log("no clocked in");
            res.status(200).json({ message: "noClockIn" });
        } else if (!existingEntry.clockOut) {
            console.log("clocked in already: "+existingEntry);
            res.status(200).json({ message: "clockedIn", data: existingEntry });
        } else {
            console.log("clocked out already: " + existingEntry);
            res.status(200).json({ message: "clockedOut", data: existingEntry });
        }
    } catch (error) {
        console.error("Error handling clock entry:", error);
        res.status(500).json({ message: "error" });
    }


});

export default Router;