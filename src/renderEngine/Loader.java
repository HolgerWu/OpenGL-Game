package renderEngine;

import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {
    //VAO: a list which has a lot of 'attribute lists' and each of them stores an VBO.
    private List<Integer> vaos = new ArrayList<>();
    //VBO: stores all kinds of things: position,color,indice,etc...,be stored in VAO.
    private List<Integer> vbos = new ArrayList<>();
    //texture: pngs to be fixed to triangle, 2^n * 2^n pixels needed.
    private List<Integer> textures = new ArrayList<>();

    public RawModel loadToVAO(float [] positions,float []textureCoords, int[] indices)
    {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        // 0 for no particular reason. 3d vector for size 3.
        storeDataInAttributeList(0,3,positions);
        // 1 too. texture coordinate only has two component (called UV coordinate system.)
        storeDataInAttributeList(1,2,textureCoords);
        unbindVAO();
        return new RawModel(vaoID,indices.length);
    }

    public int loadTexture(String filename) throws FileNotFoundException {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG",new FileInputStream("res/"+filename+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int textureID =texture.getTextureID();
        return textureID;
    }
    private int createVAO(){

        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        //activate this VAO.(the first thing)
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    public void cleanUp()
    {
        for(int vao:vaos)
        {
            GL30.glDeleteVertexArrays(vao);
        }
        for(int vbo:vbos)
        {
            GL15.glDeleteBuffers(vbo);
        }
        for(int texture:textures)
        {
            GL11.glDeleteTextures(texture);
        }
    }

    private void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    private void storeDataInAttributeList(int attributeNumber,int coordinateSize, float []data)
    {
        int vboID= GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber,coordinateSize, GL11.GL_FLOAT,false,0,0);
        //unbind the current VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
    }

    private void bindIndicesBuffer(int []indices){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        //bind an ELEMENT array buffer : tell the OpenGL to use it as an indices buffer and no need to stored into the VAO lists
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        //bind the vbo :'GL_STATIC_DRAW' means these data is only used to draw and won't change.
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        //flip the buffer so that it's ready to be read from
        buffer.flip();
        return buffer;
    }
    private FloatBuffer storeDataInFloatBuffer(float []data)
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}
