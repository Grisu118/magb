package ch.fhnw.magb;

import ch.fhnw.GLMinimal;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

/**
 * Created by benjamin on 18.02.2015.
 */
public class DrawCircle extends GLMinimal {

    float left = -1;
    float right = 1;
    float bottom;
    float top;
    float near = -100;
    float far = 100;

    public DrawCircle() {
        super();
    }

    private void drawCircle(GL4 gl, float r, boolean fill, int nPunkte) {

        vertexBuf.rewind();
        if (fill) {
            setColor(1,1,0,1);
            putVertex(0, 0, 0);
        }
        setColor(1,0,0,1);
        double phi = 2*Math.PI / nPunkte;
        for (int i = 0; i < nPunkte+1; i++) {
            putVertex((float)(r * Math.cos(phi*i)), (float)(r * Math.sin(phi*i)), 0);
            //System.out.println((r * Math.cos(phi) * i) + " : " + (r * Math.sin(phi) * i));
        }
        if (fill) {
            putVertex((float) (r * Math.cos(0)), (float) (r * Math.sin(0)), 0);
            copyBuffer(gl, nPunkte+2);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, nPunkte+2);  // Kreis zeichnen
        } else {
            copyBuffer(gl, nPunkte);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nPunkte);  // Kreis zeichnen
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL4 gl = drawable.getGL().getGL4();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float)height/width;
        top = right * aspect;
        bottom = left * aspect;
        setProjection(gl, left, right, bottom, top, near, far);
    }

    @Override
    public void display(GLAutoDrawable drawable) {        //  render image
        super.display(drawable);
        GL4 gl = drawable.getGL().getGL4();
        drawCircle(gl, 0.5f, true, 50);
    }

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new DrawCircle();
    }
}
