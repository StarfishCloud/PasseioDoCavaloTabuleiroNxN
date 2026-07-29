package br.com.davidalain;

public class CavaloRequest {
    private int dimensao;
    private String token; 

    public CavaloRequest() {}

    public CavaloRequest(int dimensao, String token) {
        this.dimensao = dimensao;
        this.token = token;
    }

    public int getDimensao() { return dimensao; }
    public void setDimensao(int dimensao) { this.dimensao = dimensao; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}