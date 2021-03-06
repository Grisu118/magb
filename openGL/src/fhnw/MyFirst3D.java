package fhnw;// -----------  Minimales 3D JOGL-Programm  (Dreieck im Raum)  ------------------------------

import fhnw.util.math.Vec3;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

public class MyFirst3D extends GLMinimal {
    // --------------------  Globale Daten  ------------------------------

    float r = 10; //, elevation = 10, azimut = 45;        // Lage des Kamera-Systems
    float left = -3, right = 3;                 // ViewingVolume im KameraSystem
    float bottom, top;
    float near = -10, far = 100;


    // -----------------------------  Methoden  --------------------------------

    void zeichneDreieck(GL4 gl) {
        vertexBuf.rewind();
        putVertex(1, 0, 0);     // Eckpunkte
        putVertex(0, 1, 0);
        putVertex(0, 0, 1);
        int nVertices = 3;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, nVertices);
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        setCameraSystem(gl, r, elevation, azimut);
        setColor(0, 1, 1, 1);
        zeichneAchsen(gl, 4, 4, 4);
        setColor(1, 0, 0, 1);
        zeichneDreieck(gl);
        translate(gl, new Vec3(2,0,0));
        rotate(gl, 60, new Vec3(0,0,1));
        setColor(1, 1, 0, 1);
        zeichneDreieck(gl);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float) height / width;
        bottom = aspect * left;
        top = aspect * right;
        // Set ViewingVolume for Orthogonalprojection
        setProjection(gl, left, right, bottom, top, near, far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        MyFirst3D sample = new MyFirst3D();
    }

}