package pl.droidsonroids.gif.sample.opengl

import android.opengl.GLES20.*
import android.opengl.Matrix
import pl.droidsonroids.gif.GifTexImage2D

class GifTexImage2DDrawer(private val gifTexImage2D: GifTexImage2D) {
    val height = gifTexImage2D.height
    val width = gifTexImage2D.width

    private var texMatrixLocation = -1
    private val texMatrix = FloatArray(16)

    private val vertexShaderCode = """
        attribute vec4 position;
        uniform mediump mat4 texMatrix;
        attribute vec4 coordinate;
        varying vec2 textureCoordinate;
        void main() {
            gl_Position = position;
            mediump vec4 outCoordinate = texMatrix * coordinate;
            textureCoordinate = vec2(outCoordinate.s, 1.0 - outCoordinate.t);
        }
        """

    private val fragmentShaderCode = """
        varying mediump vec2 textureCoordinate;
        uniform sampler2D texture;
        void main() {
             gl_FragColor = texture2D(texture, textureCoordinate);
        }
        """

    fun initialize() {
        val texNames = intArrayOf(0)
        glGenTextures(1, texNames, 0)
        glBindTexture(GL_TEXTURE_2D, texNames[0])
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        val program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, pixelShader)
        glLinkProgram(program)
        glDeleteShader(pixelShader)
        glDeleteShader(vertexShader)
        val positionLocation = glGetAttribLocation(program, "position")
        val textureLocation = glGetUniformLocation(program, "texture")
        texMatrixLocation = glGetUniformLocation(program, "texMatrix")
        val coordinateLocation = glGetAttribLocation(program, "coordinate")
        glUseProgram(program)

        val textureBuffer = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f).toFloatBuffer()
        val verticesBuffer = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f).toFloatBuffer()
        glVertexAttribPointer(coordinateLocation, 2, GL_FLOAT, false, 0, textureBuffer)
        glEnableVertexAttribArray(coordinateLocation)
        glUniform1i(textureLocation, 0)
        glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 0, verticesBuffer)
        glEnableVertexAttribArray(positionLocation)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, gifTexImage2D.width, gifTexImage2D.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
    }

    fun setDimensions(width: Int, height: Int) {
        val scaleX = width.toFloat() / gifTexImage2D.width
        val scaleY = height.toFloat() / gifTexImage2D.height
        Matrix.setIdentityM(texMatrix, 0)
        Matrix.scaleM(texMatrix, 0, scaleX, scaleY, 1f)
        Matrix.translateM(texMatrix, 0, 1 / scaleX / 2 - 0.5f, 1 / scaleY / 2 - 0.5f, 0f)
        glUniformMatrix4fv(texMatrixLocation, 1, false, texMatrix, 0)
    }

    fun draw() {
        gifTexImage2D.glTexSubImage2D(GL_TEXTURE_2D, 0)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }

    fun destroy() = gifTexImage2D.recycle()
}