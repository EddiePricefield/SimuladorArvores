package template;

import ArvoresESD.ArvoreAVL;
import ArvoresESD.ArvoreBinariaBusca;
import ArvoresESD.ArvoreVermelhoPreto;
import br.com.davidbuzatto.jsge.collision.CollisionUtils;
import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.GRAY;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.MOUSE_BUTTON_LEFT;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiCheckBox;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiConfirmDialog;
import br.com.davidbuzatto.jsge.imgui.GuiDropdownList;
import br.com.davidbuzatto.jsge.imgui.GuiLabel;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

/**
 * Nome do Projeto.
 * @author Eddie Pricefield
 * 
 * Engine JSGE
 * @author Prof. Dr. David Buzatto
 */

public class Main extends EngineFrame {
    
    //Variáveis
    private final Rectangle exibicaoArvore = new Rectangle(25, 25, 900, 550);
    
    //Câmera
    private Camera2D camera;
    private Vector2 cameraPos;
    private double cameraVel = 300;
    
    //Componentes
    private List<GuiComponent> componentes;
            
    private GuiButton btnEsquerda;
    private GuiButton btnDireita;
    private GuiButton btnBaixo;
    private GuiButton btnCima;
    
    private GuiDropdownList dropdownTipoArvore;
    private int indexAnteriorDropdown = 0;
    
    private GuiTextField textFieldValor;
    private GuiButton btnCriar;
    private GuiLabel labelValor;
    
    private GuiConfirmDialog confirmDialog;
    private int noAtual;
    
    private GuiCheckBox checkLimite;
    
    //Componentes para Criação das Árvores
    private ArvoreBinariaBusca<Integer, String> arvoreBB;
    private ArvoreAVL<Integer, String> arvoreAVL;
    private ArvoreVermelhoPreto<Integer, String> arvoreVP;
    private aesd.ds.interfaces.List<ArvoreBinariaBusca.Node<Integer, String>> nos;
    private int margemCima;
    private int margemEsquerda;
    private int raio;
    private int espacamento;
    
    public Main() {
        
        super(
            1200,                 // largura                      / width
            600,                 // altura                       / height
            "Simulador Arvores - Estruturas de Dados",      // título                       / title
            60,                  // quadros por segundo desejado / target FPS
            true,                // suavização                   / antialiasing
            false,               // redimensionável              / resizable
            false,               // tela cheia                   / full screen
            false,               // sem decoração                / undecorated
            false,               // sempre no topo               / always on top
            false                // fundo invisível              / invisible background
        );
        
    }
    
    //----------< Criar >----------//
    
    @Override
    public void create() {
        
        useAsDependencyForIMGUI();
        
        componentes = new ArrayList<>();
        camera = new Camera2D();
        
        //Variáveis para referência
        int x = 1050; int y = 75;
        int espaco = 35;
        
        //Joystick
        btnCima = new GuiButton(x, y, 30, 30, "🡹");
        btnBaixo = new GuiButton(x, y + 2 * espaco, 30, 30, "🡻");
        btnEsquerda = new GuiButton(x - espaco, y + espaco, 30, 30, "🡸");
        btnDireita = new GuiButton(x + espaco, y + espaco, 30, 30, "🡺");
        
        //Selecionar o Tipo de Árvore
        dropdownTipoArvore = new GuiDropdownList(x - 95, y + 150, 210, 30,
                List.<String>of(
                        "Árvore Binária de Busca", "Árvore AVL", "Árvore Vermelho e Preto"
                )
        );
        
        //Componentes
        labelValor = new GuiLabel(x + 8, y + 250, 10, 10, "00");
        textFieldValor = new GuiTextField(x - 95, y + 353, 100, 20, "");
        btnCriar = new GuiButton(x + 20, y + 350, 100, 25, "Inserir Valor");
        checkLimite = new GuiCheckBox(x - 70, y + 400, 30, 30, "Limitação de Digitos");
        checkLimite.setSelected(true);
        
        //Criação da Câmera
        cameraPos = new Vector2(0, 0);
        camera.target = cameraPos;
        camera.offset = new Vector2(0, 0);
        camera.rotation = 0;
        camera.zoom = 1;
        
        //Criação das Árvores
        arvoreBB = new ArvoreBinariaBusca<>();
        nos = arvoreBB.coletarParaDesenho();
        margemCima = 125;
        margemEsquerda = 75;
        raio = 20;
        espacamento = 50;
        
        //Mensagem para caso queira remover um nó
        confirmDialog = new GuiConfirmDialog( "Remoção de Nó", "Você deseja remover este nó?", "Sim", "Não", "", true);
        
        //Inserir os Componentes na Lista
        componentes.add(btnCima);
        componentes.add(btnBaixo);
        componentes.add(btnEsquerda);
        componentes.add(btnDireita);
        
        componentes.add(dropdownTipoArvore);
        
        componentes.add(textFieldValor);
        componentes.add(btnCriar);
        componentes.add(labelValor);
        componentes.add(checkLimite);
        
        componentes.add(confirmDialog);
        
    }
    
    //----------< Atualizar >----------//

    @Override
    public void update( double delta ) {
        
        atualizarComponentes(delta);
        
        //Joystick
        Color fundoBotao = LIGHTGRAY;
        Color cliqueBotao = new Color(151, 232, 255, 255);
        
        if(isKeyDown(KEY_UP) || btnCima.isMouseDown()){
            cameraPos.y += cameraVel * delta;
            btnCima.setBackgroundColor(cliqueBotao);
        } else{
           btnCima.setBackgroundColor(fundoBotao);
        }
        
        if(isKeyDown(KEY_DOWN) || btnBaixo.isMouseDown()){
            cameraPos.y -= cameraVel * delta;
            btnBaixo.setBackgroundColor(cliqueBotao);
        } else{
           btnBaixo.setBackgroundColor(fundoBotao);
        }
        
        if (isKeyDown(KEY_LEFT) || btnEsquerda.isMouseDown()) {
            cameraPos.x += cameraVel * delta;
            btnEsquerda.setBackgroundColor(cliqueBotao);
        } else {
            btnEsquerda.setBackgroundColor(fundoBotao);
        }
        
        if (isKeyDown(KEY_RIGHT) || btnDireita.isMouseDown()) {
            cameraPos.x -= cameraVel * delta;
            btnDireita.setBackgroundColor(cliqueBotao);
        } else {
            btnDireita.setBackgroundColor(fundoBotao);
        }
        
        //Atualizar Câmera
        camera.target.x = cameraPos.x;
        camera.target.y = cameraPos.y;          
        
        //Limitação de Dígitos
        if (checkLimite.isSelected()){
            if (textFieldValor.getValue().length() > 3){
                textFieldValor.setValue(textFieldValor.getValue().substring(0, 3));
            }
        }
        
        //Atualizar Valores da Árvore
        if (btnCriar.isMousePressed() || isKeyDown(KEY_ENTER)) {
            
            btnCriar.setBackgroundColor(cliqueBotao);
            
            if (dropdownTipoArvore.getSelectedItemIndex() == 0){
                arvoreBB.put(Integer.parseInt(textFieldValor.getValue()), textFieldValor.getValue());
                nos = arvoreBB.coletarParaDesenho();
            }
            
        }else{
            btnCriar.setBackgroundColor(fundoBotao);
        }
        
        //Resetar os nós ao mudar o Tipo de Árvore
        if (dropdownTipoArvore.getSelectedItemIndex() != indexAnteriorDropdown){
            
            nos.clear();
            indexAnteriorDropdown = dropdownTipoArvore.getSelectedItemIndex();
            
        }
        
        //Janela de Confirmação de Remoção do Nó
        if (confirmDialog.isButton1Pressed()) {
                    
                    switch(dropdownTipoArvore.getSelectedItemIndex()){
                        case 0: arvoreBB.delete(noAtual);
                                nos = arvoreBB.coletarParaDesenho();
                                break;
                        case 1: arvoreAVL.delete(noAtual);
                                break;
                        case 2: arvoreVP.delete(noAtual);
                                break;
                    }
                    
                    confirmDialog.hide();
                    
                } else if (confirmDialog.isButton2Pressed() || confirmDialog.isCloseButtonPressed()){
                    confirmDialog.hide();
                }

        //Remoção dos Nós da Árvore
        Vector2 mousePos = camera.getScreenToWorld(getMousePositionPoint()); //Aqui precisamos converter a posição do mouse no mundo real, para a posição dele com a interferência da câmera
        
        if ( isMouseButtonPressed( MOUSE_BUTTON_LEFT ) ) {
            
            for ( ArvoreBinariaBusca.Node<Integer, String> no : nos ) {

                Vector2 centro = new Vector2( 
                    espacamento * no.ranque + margemEsquerda, 
                    espacamento * no.nivel + margemCima
                );

                if ( CollisionUtils.checkCollisionPointCircle( mousePos, centro, raio ) ) {
                    confirmDialog.setText("Nó: " + no.key + " | Ranque: " + no.ranque + " | Nível: " + no.nivel);
                    noAtual = no.key;
                    confirmDialog.show();                    
                }
                
            }
            
        }
        
    }
    
    //----------< Desenhar >----------//
    
    @Override
    public void draw() {
        
        //Desenhando área de visualização das árvores
        fillRectangle(exibicaoArvore, WHITE);
                
        //Desenhar a Árvore (Quero que fique atrás de tudo)
        beginMode2D(camera);
        for ( ArvoreBinariaBusca.Node<Integer, String> no : nos ) {
            desenharNo( no, espacamento, espacamento );
        }
        endMode2D();
        
        //Desenhando o Plano de Fundo do Programa
        
        Color background = BEIGE;
        
        drawRectangle(exibicaoArvore, BLACK);
        fillRectangle(0, 0, 1200, 25, background);
        fillRectangle(926, 0, 277, 600, background);
        fillRectangle(0, 576, 1200, 25, background);
        fillRectangle(0, 0, 25, 600, background);

        //Círculo atrás do Joystick
        fillCircle(1065, 125, 70, WHITE);
        drawCircle(1065, 125, 70, BLACK);
        
        
        fillCircle(1065, 330, 30 , WHITE);
        drawCircle(1065, 330, 30, BLACK);
        
        //Limitação de Digitos
        fillRectangle(960, 470, 200, 40, WHITE);
        drawRectangle(960, 470, 200, 40, BLACK);
        if (checkLimite.isSelected()){
            drawText("*máximo de 3 dígitos", 995, 520, 12, RED);
        }
        
        //Textos do Projeto
        switch(dropdownTipoArvore.getSelectedItemIndex()){
            case 0 -> drawOutlinedText("Árvore Binária de Busca", 50, 50, 20, BEIGE, 1, BLACK);
            case 1 -> drawOutlinedText("Árvore AVL", 50, 50, 20, BEIGE, 1, BLACK);
            case 2 -> drawOutlinedText("Árvore Vermelho e Preto", 50, 50, 20, BEIGE, 1, BLACK);            
        }
               
        desenharComponentes();
        
    }
    
    //----------< Complementares >----------//    
    
    public void atualizarComponentes(double delta){
        
        for( GuiComponent c : componentes ){
            if (!componentes.isEmpty()){
                c.update(delta);
            }
        }
        
    }
    
    public void desenharComponentes(){
        
        for( GuiComponent c : componentes ){
            if (!componentes.isEmpty()){
                c.draw();
            }
        }
        
    }
    
    public void drawOutlinedText(String text, int posX, int posY, int fontSize, Paint color, int outlineSize, Paint outlineColor) {
        drawText(text, posX - 2, posY + 2, fontSize, GRAY);
        drawText(text, posX - outlineSize, posY - outlineSize, fontSize, outlineColor);
        drawText(text, posX + outlineSize, posY - outlineSize, fontSize, outlineColor);
        drawText(text, posX - outlineSize, posY + outlineSize, fontSize, outlineColor);
        drawText(text, posX + outlineSize, posY + outlineSize, fontSize, outlineColor);
        drawText(text, posX, posY, fontSize, color);
    }
    
    private void desenharNo( ArvoreBinariaBusca.Node<Integer, String> no, int espHorizontal, int espVertical ) {
        
        int x = espHorizontal * no.ranque + margemEsquerda;
        int y = espVertical * no.nivel + margemCima;
        
        int numDigitos = String.valueOf(no.key).length();
        
        fillCircle( x, y, raio, no.cor );
        if (numDigitos == 1) {
            drawText(no.value, x - 4 , y - 5, 15, BLACK);
        }else if (numDigitos == 2){
            drawText(no.value, x - 9 , y - 5, 15, BLACK);
        } else{
            drawText(no.value, x - 14 , y - 5, 15, BLACK);
        }
        drawCircle( x, y, raio, BLACK );
    }
    
    //----------< Instanciar Engine e Iniciá-la >----------//

    public static void main( String[] args ) {
        new Main();
    }
    
}
