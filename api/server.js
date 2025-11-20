require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const authRoutes = require('./routes/auth');
const whitelistRoutes = require('./routes/whitelist');

const app = express();
app.use(cors());
app.use(express.json());

app.use('/api', authRoutes);
app.use('/api/whitelist', whitelistRoutes);

const PORT = process.env.PORT || 5000;
mongoose.set('strictQuery', false);

async function start() {
  try {
    await mongoose.connect(process.env.MONGO_URI, { });
    console.log('MongoDB connected');
    app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
  } catch (err) {
    console.error('Failed to connect to MongoDB', err);
    process.exit(1);
  }
}

start();
