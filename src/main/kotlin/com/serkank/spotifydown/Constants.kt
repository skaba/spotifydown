package com.serkank.spotifydown

import java.io.File

const val ALL_URL_PATTERN = """https?:\/\/[^/]*open\.spotify\.com\/(track|playlist|album)\/([^\s?]+)(\?.*)?"""
const val HEADER = "https://spotifydown.com"
val MISSING_FILE = File("missing.txt")

