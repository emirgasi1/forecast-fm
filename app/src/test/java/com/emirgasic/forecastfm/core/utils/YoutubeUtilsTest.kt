package com.emirgasic.forecastfm.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class YouTubeUtilsTest {

    @Test
    fun `extractPlaylistId returns null for null url`() {
        assertNull(YouTubeUtils.extractPlaylistId(null))
    }

    @Test
    fun `extractPlaylistId returns null for blank url`() {
        assertNull(YouTubeUtils.extractPlaylistId(""))
        assertNull(YouTubeUtils.extractPlaylistId("   "))
    }

    @Test
    fun `extractPlaylistId extracts from simple url`() {
        val url = "https://www.youtube.com/playlist?list=PLxyz123"
        assertEquals("PLxyz123", YouTubeUtils.extractPlaylistId(url))
    }

    @Test
    fun `extractPlaylistId extracts from url with additional params`() {
        val url = "https://www.youtube.com/watch?v=abc&list=PLxyz123&index=2"
        assertEquals("PLxyz123", YouTubeUtils.extractPlaylistId(url))
    }

    @Test
    fun `extractPlaylistId returns null when no list param`() {
        val url = "https://www.youtube.com/watch?v=abc"
        assertNull(YouTubeUtils.extractPlaylistId(url))
    }

    @Test
    fun `extractPlaylistId returns empty string for empty list param`() {
        // Documents current behavior — returns "" not null when list= is empty
        val url = "https://www.youtube.com/watch?list="
        assertEquals("", YouTubeUtils.extractPlaylistId(url))
    }

    @Test
    fun `extractPlaylistId returns null for url without query string`() {
        val url = "https://www.youtube.com/playlist"
        assertNull(YouTubeUtils.extractPlaylistId(url))
    }
}