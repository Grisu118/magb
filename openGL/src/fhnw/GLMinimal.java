package fhnw;// -----------  Minimales 3D JOGL-Programm mit Shadern  ------------------------------
//                                                         E.Gutknecht, Feb 2015
//   adaptiert von:
//   http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
//

import fhnw.util.math.Mat4;
import fhnw.util.math.Vec3;
import com.jogamp.common.nio.Buffers;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.nio.FloatBuffer;

public class GLMinimal
        implements WindowListener, GLEventListener, KeyListener {
    // --------------------  Globale Daten  ------------------------------

    String vShaderFileName = "vShader1.glsl";        // VertexShader
    String fShaderFileName = "fShader1.glsl";        // FragmetShader

    // ------ Shader Variable Identifiers
    int projMatrixLoc, viewMatrixLoc;           // Uniform Variables
    int vPositionLocation, vColorLocation;      // Vertex Attribute Variables


    int windowWidth = 800;
    int windowHeight = 600;
    float[] clearColor = {0, 0, 1, 1};             // Fensterhintergrund (Blau)
    public GLCanvas canvas;                            // OpenGL Window


    // -------  Vertex-Daten  ------------
    int vmax = 512;                             // max. Anzahl Vertices
    int vPositionSize = 4 * Float.SIZE / 8;         // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    int vColorSize = 4 * Float.SIZE / 8;            // Anz. Bytes der rgba Werte
    int vertexSize = vPositionSize + vColorSize;     // Anz. Bytes eines Vertex
    int bufSize = vmax * vertexSize;
    float[] currentColor = {1, 1, 1, 1};          // aktuelle Farbe fuer Vertices

    // -------  Vertex-Array fuer Position- und Color-Attribute  ------------
    public FloatBuffer vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
    int vaoId;                                  // VertexArray Object Identifier
    int vertexBufId;                            // Vertex Buffer Identifier

    public int azimut = 0;
    public int elevation = 0;
    private Mat4 viewMatrix;


    // -----------------------------  Methoden  --------------------------------


    //  ------  aktuelle Zeichenfarbe setzen  -----------
    public void setColor(float r, float g, float b, float a) {
        currentColor[0] = r;
        currentColor[1] = g;
        currentColor[2] = b;
        currentColor[3] = a;
    }


    public void putVertex(float x, float y, float z)      // Vertex-Daten in Buffer speichern
    {
        vertexBuf.put(x);
        vertexBuf.put(y);
        vertexBuf.put(z);
        vertexBuf.put(1);
        vertexBuf.put(currentColor[0]);
        vertexBuf.put(currentColor[1]);
        vertexBuf.put(currentColor[2]);
        vertexBuf.put(currentColor[3]);
    }


    public void copyBuffer(GL gl, int nVertices)            // Vertex-Array in OpenGL-Buffer kopieren
    {
        vertexBuf.rewind();
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufId);
        gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, nVertices * vertexSize, vertexBuf);
    }


    public void zeichneAchsen(GL4 gl, float a, float b, float c) {
        vertexBuf.rewind();
        putVertex(0, 0, 0);           // Eckpunkte in VertexArray speichern
        putVertex(a, 0, 0);
        putVertex(0, 0, 0);
        putVertex(0, b, 0);
        putVertex(0, 0, 0);
        putVertex(0, 0, c);
        int nVertices = 6;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINES, 0, nVertices);
    }

    /**
     * translate the object system.
     *
     * @param gl OpenGL 4 Object.
     * @param v1 translate vector.
     */
    public void translate(GL4 gl, Vec3 v1) {
        viewMatrix = viewMatrix.postMultiply(Mat4.translate(v1));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public void rotate(GL4 gl, float phi, Vec3 axis) {
        viewMatrix = viewMatrix.postMultiply(Mat4.rotate(phi, axis));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public void scale(GL4 gl, Vec3 v1) {
        viewMatrix = viewMatrix.postMultiply(Mat4.scale(v1));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void setCameraSystem(GL4 gl, float r,           // Abstand der Kamera von O
                                float elevation,           // Elevationswinkel in Grad
                                float azimut)              // Azimutwinkel in Grad
    {
        float toRad = (float) (Math.PI / 180);
        float c = (float) Math.cos(toRad * elevation);
        float s = (float) Math.sin(toRad * elevation);
        float cc = (float) Math.cos(toRad * azimut);
        float ss = (float) Math.sin(toRad * azimut);
        viewMatrix = new Mat4(cc, -s * ss, c * ss, 0, 0, c, s, 0, -ss, -s * cc, c * cc, 0, 0, 0, -r, 1);
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public void setCameraSystem(GL4 gl, Vec3 a, Vec3 b, Vec3 up) {
        viewMatrix = Mat4.lookAt(a, b, up);
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void setProjection(GL4 gl, float left, float right,  // Grenzen des ViewingVolumes
                              float bottom, float top,
                              float near, float far) {
        float m00 = 2.0f / (right - left);
        ;
        float m11 = 2.0f / (top - bottom);
        float m22 = -2.0f / (far - near);
        float m03 = -(right + left) / (right - left);
        float m13 = -(top + bottom) / (top - bottom);
        float m23 = -(far + near) / (far - near);
        float m33 = 1;
        float[] projMatrix = {m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, 0, m03, m13, m23, m33};
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix, 0);
    }


    //  --------  Konstruktor  ---------------------
    public GLMinimal() {
        Frame f = new Frame("Java OpenGL");
        f.setSize(windowWidth, windowHeight);
        f.addWindowListener(this);
        f.addKeyListener(this);
        GLProfile glp = GLProfile.get(GLProfile.GL4);
        GLCapabilities glCapabilities = new GLCapabilities(glp);
        canvas = new GLCanvas(glCapabilities);
        canvas.addGLEventListener(this);
        f.add(canvas);
        f.setVisible(true);
    }

    ;


    void setupGLBuffers(GL4 gl)                                 // OpenGL Buffer
    {  // ------  OpenGl-Objekte -----------
        int[] tmp = new int[1];
        gl.glGenVertexArrays(1, tmp, 0);                         // VertexArrayObject
        vaoId = tmp[0];
        gl.glBindVertexArray(vaoId);
        gl.glGenBuffers(1, tmp, 0);                              // VertexBuffer
        vertexBufId = tmp[0];
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufId);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, bufSize,            // Speicher allozieren
                null, GL4.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(vPositionLocation);
        gl.glEnableVertexAttribArray(vColorLocation);
        gl.glVertexAttribPointer(vPositionLocation, 4, GL4.GL_FLOAT, false, vertexSize, 0);
        gl.glVertexAttribPointer(vColorLocation, 4, GL4.GL_FLOAT, false, vertexSize, vPositionSize);
    }


    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
        gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        gl.glEnable(GL4.GL_DEPTH_TEST);
        /*
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        */
        int program = GLShaders.loadShaders(gl, vShaderFileName, fShaderFileName);
        // ----- get shader variable identifiers  -------------
        projMatrixLoc = gl.glGetUniformLocation(program, "projMatrix");
        viewMatrixLoc = gl.glGetUniformLocation(program, "viewMatrix");
        vPositionLocation = gl.glGetAttribLocation(program, "vertexPosition");
        vColorLocation = gl.glGetAttribLocation(program, "vertexColor");
        float[] identityMatrix = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, identityMatrix, 0);
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, identityMatrix, 0);
        setupGLBuffers(gl);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        setColor(0, 1, 1, 1);
        zeichneAchsen(gl, 4, 4, 4);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        GLMinimal sample = new GLMinimal();
    }

    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                azimut--;
                break;
            case KeyEvent.VK_RIGHT:
                azimut++;
                break;
            case KeyEvent.VK_UP:
                elevation++;
                break;
            case KeyEvent.VK_DOWN:
                elevation--;
                break;
            default:
                break;

        }
        canvas.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}