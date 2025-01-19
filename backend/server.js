import express from 'express';
import connectDB from './config/db.js';
import dotenv from 'dotenv'
dotenv.config();
import cors from 'cors';
import userRoutes from "./routes/userRoutes.js";
import clockRoutes from "./routes/clockRoutes.js";

connectDB();

const app = express();
const PORT = process.env.PORT || 5000

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors());

// API Routes
app.use('/api/user', userRoutes);
app.use('/api/clock', clockRoutes);

// Start Server
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
