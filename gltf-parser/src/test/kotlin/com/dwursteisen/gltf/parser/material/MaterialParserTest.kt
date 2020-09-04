package com.dwursteisen.gltf.parser.material

import com.dwursteisen.gltf.parser.support.gltf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MaterialParserTest {

    private val uv by gltf("/uv/multiple_materials.gltf")

    private val cube by gltf("/mesh/cube_translated.gltf")

    private val alpha by gltf("/uv/alpha.gltf")

    @Test
    fun `parse | it returns materials`() {
        val result = MaterialParser(uv).materials()
        assertEquals(2, result.size)
        assertEquals(4, result.values.first().width)
        assertEquals(1, result.values.first().height)
        assertEquals(false, result.values.first().hasAlpha)
    }

    @Test
    fun `parse | it returns no material`() {
        val result = MaterialParser(cube).materials()
        assertEquals(0, result.size)
    }

    @Test
    fun `parse | it parses material with alpha`() {
        val result = MaterialParser(alpha).materials()
        assertEquals(1, result.size)
        assertEquals(true, result.values.first().hasAlpha)
    }
}
