package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.GRAY;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiDropdownList;
import br.com.davidbuzatto.jsge.imgui.GuiLabel;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
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
    
    //Componentes
    private List<GuiComponent> componentes;
            
    private GuiButton btnEsquerda;
    private GuiButton btnDireita;
    private GuiButton btnBaixo;
    private GuiButton btnCima;
    
    private GuiDropdownList dropdownTipoArvore;
    
    private GuiTextField textFieldValor;
    private GuiButton btnCriar;
    private GuiLabel labelValor;
    
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
        
        //Inserção de Nós
        labelValor = new GuiLabel(x + 8, y + 250, 10, 10, "00");
        textFieldValor = new GuiTextField(x - 95, y + 300, 215, 20, "");
        btnCriar = new GuiButton(x - 35, y + 330, 100, 25, "Inserir Valor");
        
        componentes.add(btnCima);
        componentes.add(btnBaixo);
        componentes.add(btnEsquerda);
        componentes.add(btnDireita);
        
        componentes.add(dropdownTipoArvore);
        
        componentes.add(textFieldValor);
        componentes.add(btnCriar);
        componentes.add(labelValor);
        
    }
    
    //----------< Atualizar >----------//

    @Override
    public void update( double delta ) {
        
        atualizarComponentes(delta);
        
    }
    
    //----------< Desenhar >----------//
    
    @Override
    public void draw() {
        
        clearBackground( BEIGE );
        
        //Desenhando área de visualização das árvores
        fillRectangle(exibicaoArvore, WHITE);
        drawRectangle(exibicaoArvore, BLACK);
        
        //Círculo atrás do Joystick
        fillCircle(1065, 330, 30 , WHITE);
        drawCircle(1065, 330, 30, BLACK);
        
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
    
    //----------< Instanciar Engine e Iniciá-la >----------//

    public static void main( String[] args ) {
        new Main();
    }
    
}
