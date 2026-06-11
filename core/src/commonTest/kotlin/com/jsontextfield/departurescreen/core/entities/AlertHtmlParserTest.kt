package com.jsontextfield.departurescreen.core.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AlertHtmlParserTest {

    @Test
    fun testStripStyleTags() {
        val html = "<style>.foo { color: red; }</style>Hello"
        val annotatedString = Alert.parseHtmlToAnnotatedString(html)
        assertEquals("Hello", annotatedString.text)
    }

    @Test
    fun testStripComments() {
        val html = "Hello<!-- This is a comment --> World"
        val annotatedString = Alert.parseHtmlToAnnotatedString(html)
        assertEquals("Hello World", annotatedString.text)
    }

    @Test
    fun testDecodeEntities() {
        val html = "Hello&nbsp;&amp;&nbsp;World &#183; here&#8217;s"
        val annotatedString = Alert.parseHtmlToAnnotatedString(html)
        assertEquals("Hello & World · here's", annotatedString.text)
    }

    @Test
    fun testBoldTag() {
        val html = "<b>Bold</b> Text"
        val annotatedString = Alert.parseHtmlToAnnotatedString(html)
        assertEquals("Bold Text", annotatedString.text)
        // Check if bold style is applied (this is harder to test on AnnotatedString without deep inspection, 
        // but we can check if spans exist)
        assertTrue(annotatedString.spanStyles.isNotEmpty())
    }

    @Test
    fun testLinkTag() {
        val html = "Click <a href=\"https://google.com\">here</a>"
        val annotatedString = Alert.parseHtmlToAnnotatedString(html)
        assertEquals("Click here", annotatedString.text)
        // Check if style is applied for link
        assertTrue(annotatedString.spanStyles.isNotEmpty())
    }

    @Test
    fun testComplexHtml() {
        val html = """
            <style>body { background: white; }</style>
            <div>
                <strong>Important:</strong><br>
                Check the <a href="https://example.com">updates</a>.
            </div>
        """.trimIndent()
        val annotatedString = Alert.parseHtmlToAnnotatedString(html)
        assertTrue(!annotatedString.text.contains("style"))
        assertTrue(annotatedString.text.contains("Important:"))
        assertTrue(annotatedString.text.contains("Check the updates."))
        assertTrue(annotatedString.spanStyles.isNotEmpty())
    }
}
