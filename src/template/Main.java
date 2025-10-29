package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiLabel;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
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
        
        //Inserção de Nós
        
        componentes.add(btnCima);
        componentes.add(btnBaixo);
        componentes.add(btnEsquerda);
        componentes.add(btnDireita);
        
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
        fillCircle(1065, 300, 30 , WHITE);
        drawCircle(1065, 300, 30, BLACK);
        
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
    
    //----------< Instanciar Engine e Iniciá-la >----------//

    public static void main( String[] args ) {
        new Main();
    }
    
}
