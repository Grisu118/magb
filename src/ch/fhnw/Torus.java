package ch.fhnw;
//  -------------   JOGL SampleProgram  (Torus) ------------

import java.awt.event.*;

import ch.fhnw.GLBase;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

public class Torus extends GLBase {
    //  --------------  globale Daten  -----------------

    float dCam = 10;                          // Abstand Kamera von O
    float elevation = 10;                      // Kamera-System
    float azimut = 20;
    float left = -0.3f, right = 0.3f;             // ViewingVolume fuer Zentralproj.
    float bottom, top;
    float near = 0.4f, far = 100;
    float a = 1, b = 1, c = 1;                      // Kanten des Quaders

    Vec3 a0 = new Vec3(1, 0, 1);
    Vec3 a1 = new Vec3(1, 0, 0);
    float phi0 = 140;
    float phi1 = 50;
    Vec3 x0 = new Vec3(3, 2, 0);
    Vec3 x1 = new Vec3(-4, 1, 1);

    int floating = 0;

    int MouseX = 0, MouseY = 0;
    boolean close = false;


    //  ------------------  Methoden  --------------------

    void rotFlaeche(GL4 gl,                          // Rotationsflaeche
                    float[] x, float[] y,                // Kurve in xy-Ebene
                    float[] nx, float[] ny,              // Normalenvektoren
                    int n2)                              // Anzahl Streifen
    {
        float todeg = (float) (180 / Math.PI);
        float dtheta = (float) (2 * Math.PI / n2);       // Drehwinkel
        float c = (float) Math.cos(dtheta);            // Drehmatrix
        float s = (float) Math.sin(dtheta);
        vertexBuf.rewind();
        int n1 = x.length;
        for (int i = 0; i < n1; i++) {
            setNormal(nx[i], ny[i], 0);
            putVertex(x[i], y[i], 0);
            setNormal(c * nx[i], ny[i], -s * nx[i]);
            putVertex(c * x[i], y[i], -s * x[i]);
        }
        int nVertices = 2 * n1;
        copyBuffer(gl, nVertices);
        pushMatrix(gl);
        for (int i = 0; i < n2; i++) {
            gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, nVertices);
            rotate(gl, todeg * dtheta, 0, 1, 0);
        }
        popMatrix(gl);
    }


    void zeichneTorus(GL4 gl, float r, float R, int n1, int n2) {
        int nn1 = n1 + 1;
        float[] x = new float[nn1];      // Kreis in xy-Ebene
        float[] y = new float[nn1];
        float[] nx = new float[nn1];     // Normalenvektoren
        float[] ny = new float[nn1];
        float dphi = 2 * (float) (Math.PI / n1), phi;
        for (int i = 0; i <= n1; i++) {
            phi = i * dphi;
            x[i] = r * (float) Math.cos(phi);
            y[i] = r * (float) Math.sin(phi);
            nx[i] = x[i];
            ny[i] = y[i];
            x[i] += R;
        }
        rotFlaeche(gl, x, y, nx, ny, n2);
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        setCameraSystem(gl, dCam, elevation, azimut);
        setColor(0.5f, 0.5f, 0.5f, 1);
        disableLighting(gl);
        zeichneAchsen(gl, 2, 2, 2);
        setColor(1, 1, 1, 1);
        enableLighting(gl);
        setLightParam(gl, 0.8f, 0, 0f, 0.4f, 0.4f, 0.4f);
        setLightPos(gl, -10, 1, 10);
        /* With SLERP
        int nPositionen = 6;
        Vec3 xt, at;
        Vec3 v = x1.subtract(x0);
        float t, phi;
        for (int i = 1; i <= nPositionen; i++) {
            pushMatrix(gl);
            t = i / (float)nPositionen;
            xt = x0.add(v.scale(t));
            phi = (float) Math.acos(a0.normalize().dot(a1.normalize()) * MathUtil.DEGREES_TO_RADIANS);
            at = a0.scale((float) ((Math.sin((1-t)*phi))/Math.sin(phi))).add(a1.scale((float) (Math.sin(t*phi) / Math.sin(phi))));
            translate(gl, xt.x, xt.y, xt.z);
            rotate(gl, phi0, at.x, at.y, at.z);
            zeichneTorus(gl, 0.2f, 0.6f, 24, 48);
            popMatrix(gl);
        }*/

        /* Quaternionen*/
        int nPositionen = 6;
        Vec3 xt;
        Vec3 v = x1.subtract(x0);
        float t;
        Quaternion q0 = Quaternion.fromAxis(a0, phi0);
        Quaternion q1 = Quaternion.fromAxis(a1, phi1);
        Quaternion qt;


        for (int i = 1; i <= nPositionen; i++) {
            pushMatrix(gl);
            t = i / (float)nPositionen;
            xt = x0.add(v.scale(t));
            translate(gl, xt.x, xt.y, xt.z);
            qt = q0.slerp(q1, t);
            Vec4 r = qt.getAxisAngle();
            rotate(gl, r.w, r.x, r.y, r.z);
            zeichneTorus(gl, 0.2f, 0.6f, 24, 48);
            popMatrix(gl);
        }

        /* Animation
        int nPositionen = 50;
        Vec3 v = x1.subtract(x0);
        pushMatrix(gl);
        float t = floating++ / (float)nPositionen;
        Vec3 xt = x0.add(v.scale(t));
        translate(gl, xt.x, xt.y, xt.z);
        rotate(gl, phi0, a0.x, a0.y, a0.z);
        zeichneTorus(gl, 0.2f, 0.6f, 24, 48);
        popMatrix(gl);
        */
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
        Torus sample = new Torus();

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
        } else if (MouseY - e.getY() < -sensibility) {
            elevation++;
            MouseY = e.getY();
        }

        canvas.repaint();
    }

}