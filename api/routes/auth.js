require('dotenv').config();
const express = require('express');
const jwt = require('jsonwebtoken');

const router = express.Router();


router.post('/login', (req, res) => {
  const { username, password } = req.body || {};

  if (!username || !password) {
    return res.status(400).json({ message: 'username and password required' });
  }

  if (
    username !== process.env.ADMIN_USERNAME ||
    password !== process.env.ADMIN_PASSWORD
  ) {
    return res.status(401).json({ message: 'Invalid credentials' });
  }

  const payload = { role: 'admin', username };
  const token = jwt.sign(payload, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN || '1h'
  });

  return res.json({ token, expiresIn: process.env.JWT_EXPIRES_IN || '1h' });
});

module.exports = router;
