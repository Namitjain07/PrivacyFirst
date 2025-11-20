const express = require('express');
const router = express.Router();
const Whitelist = require('../models/Whitelist');
const authMiddleware = require('../middleware/authMiddleware');

function isValidUrl(s) {
  if (!s || typeof s !== "string") return false;
  if (s.length > 2083) return false;

  try {
    const u = new URL(s);
    if (u.protocol !== 'http:' && u.protocol !== 'https:') return false;
    if (u.hostname.length > 255) return false;

    return true;
  } catch (e) {
    return false;
  }
}


router.get('/', authMiddleware, async (req, res) => {
  try {
    const docs = await Whitelist.find().sort({ createdAt: -1 }).lean();
    const urls = docs.map(d => d.url);
    res.json({ urls, count: urls.length });
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

router.post('/add', authMiddleware, async (req, res) => {
  try {
    const { url } = req.body || {};
    if (!url || typeof url !== 'string') {
      return res.status(400).json({ message: 'url is required in body' });
    }
    if (!isValidUrl(url)) {
      return res.status(400).json({ message: 'invalid url format' });
    }

    const existing = await Whitelist.findOne({ url });
    if (existing) {
      return res.status(409).json({ message: 'url already whitelisted' });
    }

    const doc = await Whitelist.create({ url, addedBy: req.user.username || 'admin' });
    res.status(201).json({ message: 'added', doc });
  } catch (err) {
    res.status(500).json({ message: 'Server error', error: err.message });
  }
});


router.put('/update', authMiddleware, async (req, res) => {
  try {
    const { oldUrl, newUrl } = req.body || {};
    if (!oldUrl || !newUrl) return res.status(400).json({ message: 'oldUrl and newUrl required' });
    if (!isValidUrl(newUrl)) return res.status(400).json({ message: 'newUrl invalid' });

    const updated = await Whitelist.findOneAndUpdate(
      { url: oldUrl },
      { url: newUrl },
      { new: true }
    );

    if (!updated) return res.status(404).json({ message: 'oldUrl not found' });
    res.json({ message: 'updated', doc: updated });
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});


router.delete('/delete', authMiddleware, async (req, res) => {
  try {
    const { url } = req.body || {};
    if (!url) return res.status(400).json({ message: 'url required' });

    const removed = await Whitelist.findOneAndDelete({ url });
    if (!removed) return res.status(404).json({ message: 'url not found' });
    res.json({ message: 'deleted', doc: removed });
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
