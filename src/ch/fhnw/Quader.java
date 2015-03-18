package ch.fhnw;//  -------------   JOGL SampleProgram  (Quader) ------------

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Quader extends GLBase {
    //  --------------  globale Daten  -----------------

    float dCam = 10;                          // Abstand Kamera von O
    float elevation = 0;                      // Kamera-System
    float azimut = 0;
    float left = -0.2f, right = 0.2f;             // ViewingVolume fuer Zentralproj.
    float bottom, top;
    float near = 0.4f, far = 100;
    float a = 1, b = 1, c = 1;                      // Kanten des Quaders
    int MouseX = 0, MouseY = 0;


    //  ------------------  Methoden  --------------------


    void gitterBoden(GL4 gl, int nx, int nz,    // Anzahl Linien
                     float dx, float dz)        // Abstaende
    {
        float xmax = 0.5f * (nx - 1) * dx;
        float xmin = -xmax;
        float zmax = 0.5f * (nz - 1) * dz;
        float zmin = -zmax;
        float x, z;
        vertexBuf.rewind();
        z = zmin;
        for (int i = 0; i < nz; i++)       // Linien parallel x-Achse
        {
            putVertex(xmin, 0, z);
            putVertex(xmax, 0, z);
            z += dz;
        }
        x = xmin;
        for (int i = 0; i < nx; i++)       // Linien parallel z-Achse
        {
            putVertex(x, 0, zmin);
            putVertex(x, 0, zmax);
            x += dx;
        }
        int nVertices = 2 * nx + 2 * nz;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINES, 0, nVertices);
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        setCameraSystem(gl, dCam, elevation, azimut);
        pushMatrix(gl);
        setColor(1, 1, 1, 1);
        disableLighting(gl);
        gitterBoden(gl, 10, 10, 1f, 2.0f);
        setColor(0, 1, 1, 1);
        setLightPos(gl, -10, 100, 10);
        enableLighting(gl);
        moebius(gl);
        setColor(1,0,0,1);
        translate(gl, 0,0,2);
        rotate(gl, 180, 1,1,0);
        pushMatrix(gl);
        moebius(gl);
    }

    private void moebius(GL4 gl) {
        for (int i = 0; i < 360; i += 2) {
            rotate(gl, i, 0, 1, 0);
            translate(gl, 2, 0, 0);
            rotate(gl, i / 2, 0, 0, 1);
            zeichneQuader(gl, 0.5f, 0.05f, 0.05f);
            popMatrix(gl);
            pushMatrix(gl);
        }
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
        setPerspectiveProjection(gl, left, right, bottom, top, near, far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        Quader sample = new Quader();
    }


    //  ---------  Keyboard-Events  ------------------

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                elevation++;
                canvas.repaint();
                break;
            case KeyEvent.VK_DOWN:
                elevation--;
                canvas.repaint();
                break;
            case KeyEvent.VK_LEFT:
                azimut--;
                canvas.repaint();
                break;
            case KeyEvent.VK_RIGHT:
                azimut++;
                canvas.repaint();
                break;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int sensibility = 3;
        if (MouseX - e.getX() > sensibility) {
            azimut++;
            MouseX = e.getX();
        } else if (MouseX - e.getX() < -sensibility) {
            azimut--;
            MouseX = e.getX();
        }

        if (MouseY - e.getY() > sensibility) {
            elevation--;
            MouseY = e.getY();
        } else if (MouseY - e.getY() < -sensibility){
            elevation++;
            MouseY = e.getY();
        }

        canvas.repaint();
    }

}