package ch.fhnw.magb;

import ch.fhnw.GLMinimal;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import ch.fhnw.util.math.Mat3;
import ch.fhnw.util.math.Vec3;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.event.KeyEvent;

/**
 * Created by benjamin on 01.03.2015.
 */
public class Ikosaeder extends GLMinimal {

    float left = -3;
    float right = 3;
    float bottom;
    float top;
    float near = -100;
    float far = 100;

    private static final float X = 0.525731112119133606f;
    private static final float Z = 0.850650808352039932f;

    private static final float[][] VDATA = {
        {-X, 0.0f, Z}, {X, 0.0f, Z}, {-X, 0.0f, -Z}, {X, 0.0f, -Z},
        {0.0f, Z, X}, {0.0f, Z, -X}, {0.0f, -Z, X}, {0.0f, -Z, -X},
        {Z, X, 0.0f}, {-Z, X, 0.0f}, {Z, -X, 0.0f}, {-Z, -X, 0.0f}
    };

    private static final int[][] TINDICES= {
        {1,4,0}, {4,9,0}, {4,5,9}, {8,5,4}, {1,8,4},
        {1,10,8}, {10,3,8}, {8,3,5}, {3,2,5}, {3,7,2},
        {3,10,7}, {10,6,7}, {6,11,7}, {6,0,11}, {6,1,0},
        {10,1,6}, {11,0,9}, {2,11,9}, {5,2,9}, {11,2,7}
    };

    private static final Vec3 TOLIGHT = new Vec3(-1,2,1).normalize();

    private float winkel = 0;


    public Ikosaeder() {
        super();
    }

    private void drawIcosaeder(GL4 gl) {
        if (++winkel > 360) {
            winkel = 0;
        }


        vertexBuf.rewind();
        float brightness = 0;
        for (int i = 0; i < 20; i++) {
            Vec3 A = new Vec3(VDATA[TINDICES[i][0]][0], VDATA[TINDICES[i][0]][1], VDATA[TINDICES[i][0]][2]);
            Vec3 B = new Vec3(VDATA[TINDICES[i][1]][0], VDATA[TINDICES[i][1]][1], VDATA[TINDICES[i][1]][2]);
            Vec3 C = new Vec3(VDATA[TINDICES[i][2]][0], VDATA[TINDICES[i][2]][1], VDATA[TINDICES[i][2]][2]);

            Mat3 rot = Mat3.rotate(winkel, TOLIGHT);

            A = rot.transform(A);
            B = rot.transform(B);
            C = rot.transform(C);

            //n = (B-A) x (C-A)
            Vec3 n = B.subtract(A).cross(C.subtract(A)).normalize();
            brightness = 0.5f + 0.4f * n.dot(TOLIGHT);  // Lambert-Gesetz
            if ( brightness < 0) brightness = 0;
            setColor(brightness, 0, 0, 1);   // Rot-Stufe
            putVertex(A.x(), A.y(), A.z());
            putVertex(B.x(), B.y(), B.z());
            putVertex(C.x(), C.y(), C.z());

            setColor(0,0.2f,0.2f,1);

        }
        copyBuffer(gl, 20*3);   // VertexArray in OpenGL-Buffer kopieren
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 20*3);
    }

    private Vec3 shadow(Vec3 p, Vec3 v) {
        Vec3 yv = new Vec3(0, 0.01f, 0);
        Vec3 n = new Vec3(0,1,0);
        float t = -n.dot(p)/n.dot(v);
        Vec3 pp = p.add(v.scale(t)); //FINISH
        pp = pp.add(yv);
        return pp;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL4 gl = drawable.getGL().getGL4();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float)height/width;
        top = right * aspect;
        bottom = left * aspect;
        setProjection(gl, left, right, bottom, top, near, far);
        elevation = 14;
        azimut = 10;
        setCameraSystem(gl, 10, elevation, azimut);
    }



    @Override
    public void display(GLAutoDrawable drawable) {        //  render image
        super.display(drawable);
        GL4 gl = drawable.getGL().getGL4();
        drawIcosaeder(gl);
        setCameraSystem(gl, 10, elevation, azimut);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        FPSAnimator ani = new FPSAnimator(drawable, 70);
        ani.start();
    }

    private static Mat3 rotX(double a) {
        double alpha = Math.toRadians(a);
        return new Mat3(1,0,0,0,(float)Math.cos(alpha), (float)Math.sin(alpha), 0, -(float)Math.sin(alpha), (float)Math.cos(alpha));
    }
    private static Mat3 rotY(double a) {
        double alpha = Math.toRadians(a);
        return new Mat3((float)Math.cos(alpha), 0, -(float)Math.sin(alpha), 0, 1, 0, (float)Math.sin(alpha), 0, (float)Math.cos(alpha));
    }
    private static Mat3 rotZ(double a) {
        double alpha = Math.toRadians(a);
        return new Mat3((float)Math.cos(alpha), (float)Math.sin(alpha), 0, -(float)Math.sin(alpha),(float)Math.cos(alpha), 0, 0, 0, 1);
    }

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new Ikosaeder();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
    }
}
