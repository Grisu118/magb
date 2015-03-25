package ch.fhnw;// -----------  3D JOGL Basis-Programm mit Shadern  ------------------------------
//                                                         E.Gutknecht, Maerz 2015
//   adaptiert von:
//   http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
//
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import java.nio.*;
import java.util.*;

import com.jogamp.common.nio.*;
import ch.fhnw.util.math.*;


public class GLBase
       implements WindowListener, GLEventListener, KeyListener, MouseMotionListener
{
    // --------------------  Globale Daten  ------------------------------

    String vShaderFileName = "vShader2.glsl";        // VertexShader
    String fShaderFileName = "fShader1.glsl";        // FragmetShader

    // ------ Shader Variable Identifiers
    int projMatrixLoc, viewMatrixLoc, normalMatrixLoc;                 // Uniform Variables
    int LightingLoc, LightDirectionLoc, LightColorLoc, AmbientLoc;

    int vPositionLocation, vColorLocation, vNormalLocation;      // Vertex Attribute Variables


    int windowWidth = 800;
    int windowHeight = 600;
    float[] clearColor = {0,0,1,1};             // Fensterhintergrund (Blau)
    GLCanvas canvas;                            // OpenGL Window


    // -------  ModelView-Matrix --------
    Mat4 viewMatrix = Mat4.ID;
    Stack<Mat4> matrixStack = new Stack<>();


    // -------  Beleuchtung  ------------
    int Lighting = 0;                           // beleuchtung ein (1) / aus (0)
    float[] LightDirection = {0,0,1,0};         // Richtung zur Lichtquelle
    float[] LightColor={1,1,1,1};
    float[] Ambient={0.4f,0.4f,0.4f,1};         // ambientes Licht



    // -------  Vertex-Daten  ------------
    int vmax = 1024;                            // max. Anzahl Vertices
    int vPositionSize = 4*Float.SIZE/8;         // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    int vColorSize = 4*Float.SIZE/8;            // Anz. Bytes der rgba Werte
    int vNormalSize = 4*Float.SIZE/8;           // Anz. Bytes der Normalen
    int vertexSize = vPositionSize + vColorSize + vNormalSize;     // Anz. Bytes eines Vertex
    int bufSize = vmax*vertexSize;
    float[] currentColor = { 1,1,1,1};          // aktuelle Farbe fuer Vertices
    float[] currentNormal = { 1,0,0,0};         // aktuelle Normale fuer Vertices

    // -------  Vertex-Array fuer Position- und Color-Attribute  ------------
    public FloatBuffer vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
    int vaoId;                                  // VertexArray Object Identifier
    int vertexBufId;                            // Vertex Buffer Identifier


    // -----------------------------  Methoden  --------------------------------


    //  ------  aktuelle Zeichenfarbe setzen  -----------
    public void setColor(float r, float g, float b, float a)
    {  currentColor[0] = r;
       currentColor[1] = g;
       currentColor[2] = b;
       currentColor[3] = a;
    }

    //  ------  aktuelle Normale setzen  -----------
    public void setNormal(float x, float y, float z)
    {  currentNormal[0] = x;
       currentNormal[1] = y;
       currentNormal[2] = z;
       currentNormal[3] = 0;
    }

    protected void putVertex(float x, float y, float z)      // Vertex-Daten in Buffer speichern
    {  vertexBuf.put(x);
       vertexBuf.put(y);
       vertexBuf.put(z);
       vertexBuf.put(1);
       vertexBuf.put(currentColor[0]);
       vertexBuf.put(currentColor[1]);
       vertexBuf.put(currentColor[2]);
       vertexBuf.put(currentColor[3]);
       vertexBuf.put(currentNormal[0]);
       vertexBuf.put(currentNormal[1]);
       vertexBuf.put(currentNormal[2]);
       vertexBuf.put(currentNormal[3]);
    }


    protected void copyBuffer(GL gl, int nVertices)            // Vertex-Array in OpenGL-Buffer kopieren
    {  vertexBuf.rewind();
       gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, nVertices*vertexSize, vertexBuf);
    }


    // -------  Beleuchtung einschalten  ------
    void enableLighting(GL4 gl)
    { Lighting = 1;
      gl.glUniform1i(LightingLoc, Lighting);
    }


    // -------  Beleuchtung ausschalten  ------
    void disableLighting(GL4 gl)
    { Lighting = 0;
      gl.glUniform1i(LightingLoc, Lighting);
    }


    //  --------  Position Lichtquelle (im Kamera-System) -----
    public void setLightPos(GL4 gl, double x, double y, double z)
    { Vec4 tmp = new Vec4(x,y,z,0);
      Vec4 toLight = viewMatrix.transform(tmp);
      gl.glUniform4fv(LightDirectionLoc, 1, toLight.toArray(),0);
    }


    //  --------  Beleuchtungsparameter  -----------
    void setLightParam(GL4 gl, float rLight, float gLight, float bLight,  // Lichtfarbe
                       float rAmbient, float gAmbient, float bAmbient)    // ambientes Licht
    { LightColor[0] = rLight;
      LightColor[1] = gLight;
      LightColor[2] = bLight;
      Ambient[0] = rAmbient;
      Ambient[1] = gAmbient;
      Ambient[2] = bAmbient;
      gl.glUniform4fv(LightColorLoc, 1, LightColor,0);
      gl.glUniform4fv(AmbientLoc, 1, Ambient,0);
    }


    //  ---------  Operationen fuer ModelView-Matrix  --------------------
    void rotate(GL4 gl, float phi, float x, float y, float z)              // Rechtsmultiplikation mit R
    {  viewMatrix = viewMatrix.postMultiply(Mat4.rotate(phi,x,y,z));
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
       gl.glUniformMatrix4fv(normalMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    void translate(GL4 gl, float x, float y, float z)                      // Rechtsmultiplikation mit T
    {  viewMatrix = viewMatrix.postMultiply(Mat4.translate(x,y,z));
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
       gl.glUniformMatrix4fv(normalMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    void scale(GL4 gl, float x, float y, float z)                      // Rechtsmultiplikation mit T
    {  viewMatrix = viewMatrix.postMultiply(Mat4.scale(x,y,z));
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
       gl.glUniformMatrix4fv(normalMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    void pushMatrix(GL4 gl)                // ModelView-Matrix speichern
    {  matrixStack.push(viewMatrix);
    }

    void popMatrix(GL4 gl)                 // ModelView-Matrix vom Stack holen
    {  viewMatrix = matrixStack.pop();
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public void setCameraSystem(GL4 gl, float r,           // Abstand der Kamera von O
                                float elevation,           // Elevationswinkel in Grad
                                float azimut)              // Azimutwinkel in Grad
    {  float toRad = (float)(Math.PI/180);
       float c = (float)Math.cos(toRad*elevation);
       float s = (float)Math.sin(toRad*elevation);
       float cc = (float)Math.cos(toRad*azimut);
       float ss = (float)Math.sin(toRad*azimut);
       viewMatrix = new Mat4(cc, -s*ss, c*ss, 0, 0, c, s, 0, -ss, -s*cc, c*cc, 0, 0, 0, -r, 1);
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
       gl.glUniformMatrix4fv(normalMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void setCameraSystem(GL4 gl, Vec3 A, Vec3 B, Vec3 up)   // LookAt
    {  viewMatrix = Mat4.lookAt(A,B,up);
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
       gl.glUniformMatrix4fv(normalMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void setProjection(GL4 gl, float left, float right,  // Grenzen des ViewingVolumes
                                      float bottom, float top,
                                      float near, float far)
    {   float m00 = 2.0f / (right-left);;
        float m11 = 2.0f / (top-bottom);
        float m22 = -2.0f / (far-near);
        float m03 = - (right + left) / (right-left);
        float m13 = - (top + bottom) / (top-bottom);
        float m23 = - (far + near) / (far-near);
        float m33 = 1;
        float[] projMatrix = {m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, 0, m03, m13, m23, m33 };
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix, 0);
    }


    public void setPerspectiveProjection(GL4 gl, float left, float right,  // Grenzen des ViewingVolumes
                                      float bottom, float top,
                                      float near, float far)
    {   Mat4 projMatrix = Mat4.perspective(left,right,bottom,top,near,far);
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix.toArray(), 0);
    }


    // -------  Zeichenmethoden  --------------------------------

    protected void zeichneAchsen(GL4 gl, float a, float b, float c)
    {  vertexBuf.rewind();
       putVertex(0,0,0);           // Eckpunkte in VertexArray speichern
       putVertex(a,0,0);
       putVertex(0,0,0);
       putVertex(0,b,0);
       putVertex(0,0,0);
       putVertex(0,0,c);
       int nVertices = 6;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL4.GL_LINES, 0, nVertices);
    }


    Vec3 dreiecksNormale(Vec3 a, Vec3 b, Vec3 c)
    {  Vec3 u = a.subtract(b);
       Vec3 v = a.subtract(c);
       Vec3 n = u.cross(v);
       return n;
    }


    void zeichneViereck(GL4 gl, Vec3  a, Vec3 b, Vec3 c, Vec3 d)
    {  Vec3 n1 = dreiecksNormale(a,b,c);
       Vec3 n2 = dreiecksNormale(c,d,a);
       Vec3 normale =  (n1.add(n2)).scale(0.5f);  // mittlere Normale
       setNormal(normale.x, normale.y, normale.z);
       vertexBuf.rewind();
       putVertex(a.x, a.y, a.z);
       putVertex(b.x, b.y, b.z);
       putVertex(c.x, c.y, c.z);
       putVertex(d.x, d.y, d.z);
       int nVertices = 4;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL4.GL_TRIANGLE_FAN,0,nVertices);
    }


    void zeichneQuader(GL4 gl, float a, float b, float c)
    {  a *= 0.5f;
       b *= 0.5f;
       c *= 0.5f;
       Vec3 A = new Vec3( a, -b, c);        // Boden
       Vec3 B = new Vec3( a, -b,-c);
       Vec3 C = new Vec3(-a, -b,-c);
       Vec3 D = new Vec3(-a, -b, c);
       Vec3 AA = new Vec3( a, b, c);        // Deckflaeche
       Vec3 BB = new Vec3( a, b,-c);
       Vec3 CC = new Vec3(-a, b,-c );
       Vec3 DD = new Vec3(-a, b, c);
       zeichneViereck(gl,D,C,B,A);          // Boden
       zeichneViereck(gl,AA,BB,CC,DD);      // Deckflaeche
       zeichneViereck(gl,A,B,BB,AA);        // Seitenflaechen
       zeichneViereck(gl,B,C,CC,BB);
       zeichneViereck(gl,D,DD,CC,C);
       zeichneViereck(gl,A,AA,DD,D);
    }


    //  --------  Konstruktor  ---------------------
    public GLBase()
    {  Frame f = new Frame("Java OpenGL");
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL4);
       GLCapabilities glCapabilities = new GLCapabilities(glp);
       canvas = new GLCanvas(glCapabilities);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
    };


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
       gl.glEnableVertexAttribArray(vNormalLocation);
       gl.glVertexAttribPointer(vPositionLocation, 4, GL4.GL_FLOAT, false, vertexSize, 0);
       gl.glVertexAttribPointer(vColorLocation, 4, GL4.GL_FLOAT, false, vertexSize, vPositionSize);
       gl.glVertexAttribPointer(vNormalLocation, 4, GL4.GL_FLOAT, false, vertexSize, vPositionSize+vColorSize);
   }


    @Override
    public void init(GLAutoDrawable drawable)
    {  GL4 gl = drawable.getGL().getGL4();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
       gl.glEnable(GL4.GL_DEPTH_TEST);
       int program = GLShaders.loadShaders(gl, vShaderFileName, fShaderFileName);
       // ----- get shader variable identifiers  -------------
       projMatrixLoc = gl.glGetUniformLocation(program, "projMatrix");
       viewMatrixLoc = gl.glGetUniformLocation(program, "viewMatrix");
       normalMatrixLoc = gl.glGetUniformLocation(program, "normalMatrix");
       LightingLoc = gl.glGetUniformLocation(program, "Lighting");
       LightDirectionLoc = gl.glGetUniformLocation(program, "LightDirection");
       LightColorLoc = gl.glGetUniformLocation(program, "LightColor");
       AmbientLoc = gl.glGetUniformLocation(program, "Ambient");
       vPositionLocation = gl.glGetAttribLocation(program, "vertexPosition");
       vColorLocation = gl.glGetAttribLocation(program, "vertexColor");
       vNormalLocation = gl.glGetAttribLocation(program, "vertexNormal");
       float[] identityMatrix = {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
       gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, identityMatrix, 0);
       gl.glUniformMatrix4fv(projMatrixLoc, 1, false, identityMatrix, 0);
       gl.glUniformMatrix4fv(normalMatrixLoc, 1, false, identityMatrix, 0);
       gl.glUniform1i(LightingLoc, Lighting);
       gl.glUniform4fv(LightDirectionLoc, 1, LightDirection,0);
       gl.glUniform4fv(LightColorLoc, 1, LightColor,0);
       gl.glUniform4fv(AmbientLoc, 1, Ambient,0);
       setupGLBuffers(gl);
       canvas.addKeyListener(this);
        canvas.addMouseMotionListener(this);
   }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL4 gl = drawable.getGL().getGL4();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable)
    { GL4 gl = drawable.getGL().getGL4();
      gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
      setColor(0, 1, 1, 1);
      zeichneAchsen(gl, 4,4,4);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { GLBase sample = new GLBase();
    }

    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e)
    {  System.exit(0);
    }
    public void windowActivated(WindowEvent e) {  }
    public void windowClosed(WindowEvent e) {  }
    public void windowDeactivated(WindowEvent e) {  }
    public void windowDeiconified(WindowEvent e) {  }
    public void windowIconified(WindowEvent e) {  }
    public void windowOpened(WindowEvent e) {  }


    //  ----------  Keyboard-Events  --------------
    public void keyPressed(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }
    public void keyReleased(KeyEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}