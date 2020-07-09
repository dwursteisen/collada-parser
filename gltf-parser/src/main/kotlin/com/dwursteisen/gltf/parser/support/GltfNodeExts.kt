package com.dwursteisen.gltf.parser.support

import com.adrienben.tools.gltf.models.GltfNode
import com.curiouscreature.kotlin.math.*

val GltfNode.transformation: Mat4
    get() {
        val t = translation.let { Float3(it.x, it.y, it.z) }
            .let { translation(it) }

        val r = rotation.let { Quaternion(it.i, it.j, it.k, it.a) }
            .let { Mat4.from(it) }

        val s = scale.let { Float3(it.x, it.y, it.z) }
            .let { scale(it) }

        return t * r * s
    }