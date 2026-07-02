package com.example.myapplication.engine

import org.junit.Test
import org.junit.Assert.*
import kotlin.math.*

class StemSeparatorTest {

    @Test
    fun testStemNames() {
        assertEquals(4, StemSeparator.STEM_NAMES.size)
        assertEquals("Sub-Bass", StemSeparator.STEM_NAMES[0])
        assertEquals("Bass", StemSeparator.STEM_NAMES[1])
        assertEquals("Mids", StemSeparator.STEM_NAMES[2])
        assertEquals("Highs", StemSeparator.STEM_NAMES[3])
    }

    @Test
    fun testFrequencyRanges() {
        assertEquals(StemSeparator.SUB_BASS_RANGE, 20f..300f)
        assertEquals(StemSeparator.BASS_RANGE, 300f..1200f)
        assertEquals(StemSeparator.MID_RANGE, 1200f..6000f)
        assertEquals(StemSeparator.HIGH_RANGE, 6000f..20000f)
    }

    @Test
    fun testProcessFloatBufferProducesStems() {
        val separator = StemSeparator()
        val buffer = FloatArray(2048) { sin(it * 0.1).toFloat() * 0.5f }
        val result = separator.processFloatBuffer(buffer, 2048)

        assertNotNull(result)
        assertEquals(4, result.stems.size)
        assertNotNull(result.dominantStem)
        assertTrue(result.separationConfidence in 0f..1f)
    }

    @Test
    fun testSilentBufferProducesLowEnergy() {
        val separator = StemSeparator()
        val buffer = FloatArray(2048) { 0f }
        val result = separator.processFloatBuffer(buffer, 2048)

        val totalEnergy = result.stems.sumOf { it.energy.toDouble() }.toFloat()
        assertTrue("Silent buffer should have near-zero energy", totalEnergy < 0.01f)
    }

    @Test
    fun testDominantStemIsHighestEnergy() {
        val separator = StemSeparator()
        val buffer = FloatArray(2048) { sin(it * 0.05).toFloat() * 0.8f }
        val result = separator.processFloatBuffer(buffer, 2048)

        val dominant = result.dominantStem
        assertNotNull(dominant)
        result.stems.forEach { stem ->
            if (stem.name != dominant!!.name) {
                assertTrue(
                    "Dominant stem should have highest energy",
                    dominant!!.energy >= stem.energy
                )
            }
        }
    }
}
