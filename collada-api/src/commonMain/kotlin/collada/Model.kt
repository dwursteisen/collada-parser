package collada

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
class Model(
    @SerialId(1)
    val mesh: Mesh,
    @SerialId(2)
    @Polymorphic val armature: ArmatureDescription
) {

    companion object {
        @ExperimentalStdlibApi
        fun readJson(data: ByteArray): Model {
            val deserializer = Json(context = serialModule())
            return deserializer.parse(serializer(), data.decodeToString())
        }

        fun readProtobuf(data: ByteArray): Model {
            val deserializer = ProtoBuf(context = serialModule())
            return deserializer.load(serializer(), data)
        }

        fun serialModule(): SerialModule {
            return SerializersModule {
                polymorphic<ArmatureDescription> {
                    Armature::class with Armature.serializer()
                    EmptyArmature::class with EmptyArmature.serializer()
                }
            }
        }

        @ExperimentalStdlibApi
        fun writeJson(model: Model): ByteArray {
            val serializer = Json(context = serialModule())
            return serializer.stringify(Model.serializer(), model).encodeToByteArray()
        }

        fun writeProtobuf(model: Model): ByteArray {
            val serializer = ProtoBuf(context = serialModule())
            return serializer.dump(Model.serializer(), model)
        }
    }
}
