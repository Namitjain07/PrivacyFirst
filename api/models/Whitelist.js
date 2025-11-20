const mongoose = require('mongoose');

const WhitelistSchema = new mongoose.Schema({
  url: { type: String, required: true, unique: true },
  addedBy: { type: String, default: 'admin' },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Whitelist', WhitelistSchema);
