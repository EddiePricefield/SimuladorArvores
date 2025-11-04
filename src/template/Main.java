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
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import static br.com.davidbuzatto.jsge.math.MathUtils.lerp;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Nome do Projeto.
 *
 * @author Eddie Pricefield
 *
 * Engine JSGE
 * @author Prof. Dr. David Buzatto
 */
public class Main extends EngineFrame {

    //Variáveis
    private final Rectangle exibicaoArvore = new Rectangle(25, 25, 900, 550);
    String valorAnterior;
    private int noAtual;

    //Câmera
    private Camera2D camera;
    private Vector2 cameraPos;
    private final double cameraVel = 300;

    //Componentes
    private List<GuiComponent> componentes;

    private GuiButton btnEsquerda;
    private GuiButton btnDireita;
    private GuiButton btnBaixo;
    private GuiButton btnCima;
    private GuiButton btnMais;
    private GuiButton btnMenos;
    private GuiButton btnReset;

    private GuiDropdownList dropdownTipoArvore;
    private int indexAnteriorDropdown = 0;

    private GuiTextField textFieldValor;
    private GuiButton btnCriar;
    private GuiButton btnLimpar;

    private GuiConfirmDialog confirmDeletarNo;
    private GuiConfirmDialog confirmDeletarArvore;

    private GuiCheckBox checkLimite;

    //Variáveis para a Animação
    private boolean animando;
    private String animValue;
    private Vector2 animIni;
    private Vector2 animPos;
    private Vector2 animFim;
    private double animTempo;
    private double animVel;
    private double ranqueAnterior = -1;
    private double nivelAnterior = -1;
    boolean igualDeNovo = false;

    //Variáveis para Criação das Árvores
    private ArvoreBinariaBusca<Integer, String> arvoreBB;
    private ArvoreAVL<Integer, String> arvoreAVL;
    private ArvoreVermelhoPreto<Integer, String> arvoreVP;
    private aesd.ds.interfaces.List<ArvoreBinariaBusca.Node<Integer, String>> nosBB;
    private aesd.ds.interfaces.List<ArvoreAVL.Node<Integer, String>> nosAVL;
    private aesd.ds.interfaces.List<ArvoreVermelhoPreto.Node<Integer, String>> nosVP;
    private int margemCima;
    private int margemEsquerda;
    private int raio;
    private int espacamento;

    public Main() {

        super(
                1200, // largura                      / width
                600, // altura                       / height
                "Simulador Arvores - Estruturas de Dados", // título                       / title
                60, // quadros por segundo desejado / target FPS
                true, // suavização                   / antialiasing
                false, // redimensionável              / resizable
                false, // tela cheia                   / full screen
                false, // sem decoração                / undecorated
                false, // sempre no topo               / always on top
                false // fundo invisível              / invisible background
        );

    }

    //----------< Criar >----------//
    @Override
    public void create() {

        useAsDependencyForIMGUI();

        componentes = new ArrayList<>();
        camera = new Camera2D();

        //Variáveis para referência
        int x = 1048;
        int y = 75;
        int espaco = 35;

        //Joystick
        btnCima = new GuiButton(x, y, 30, 30, "🡹");
        btnBaixo = new GuiButton(x, y + 2 * espaco, 30, 30, "🡻");
        btnEsquerda = new GuiButton(x - espaco, y + espaco, 30, 30, "🡸");
        btnDireita = new GuiButton(x + espaco, y + espaco, 30, 30, "🡺");
        btnMenos = new GuiButton(x - 3 * espaco + 5, y + espaco + 5, 20, 20, "➖");
        btnMais = new GuiButton(x + 3 * espaco + 5, y + espaco + 5, 20, 20, "➕");
        btnReset = new GuiButton(x + espaco / 4, y + espaco + espaco / 4, 15, 15, "R");

        //Selecionar o Tipo de Árvore
        dropdownTipoArvore = new GuiDropdownList(x - 93, y + 150, 210, 30,
                List.<String>of(
                        "Árvore Binária de Busca", "Árvore AVL", "Árvore Vermelho e Preto"
                )
        );

        //Componentes
        textFieldValor = new GuiTextField(x - 105, y + 353, 100, 20, "");
        btnCriar = new GuiButton(x + 5, y + 350, 100, 25, "Inserir Valor");
        btnLimpar = new GuiButton(x + 110, y + 350, 25, 25, "♻");
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
        arvoreAVL = new ArvoreAVL<>();
        arvoreVP = new ArvoreVermelhoPreto<>();

        nosBB = arvoreBB.coletarParaDesenho();
        nosAVL = arvoreAVL.coletarParaDesenho();
        nosVP = arvoreVP.coletarParaDesenho();
        margemCima = 125;
        margemEsquerda = 75;
        raio = 20;
        espacamento = 50;

        //Animação
        animando = false;
        animIni = new Vector2(margemEsquerda, margemCima);
        animVel = 1.5;

        //Mensagem para caso queira remover um nó
        confirmDeletarNo = new GuiConfirmDialog("Remoção de Nó", "Você tem certeza que deseja remover este nó?", "Sim", "Não", "", true);
        confirmDeletarArvore = new GuiConfirmDialog("Limpar a Tela de Nós", "Esta ação irá excluir todos os nós da árvore.\nDeseja prosseguir?", "Sim", "Não", "", true);

        //Inserir os Componentes na Lista
        componentes.add(btnCima);
        componentes.add(btnBaixo);
        componentes.add(btnEsquerda);
        componentes.add(btnDireita);
        componentes.add(btnMenos);
        componentes.add(btnMais);
        componentes.add(btnReset);

        componentes.add(dropdownTipoArvore);

        componentes.add(textFieldValor);
        componentes.add(btnCriar);
        componentes.add(btnLimpar);
        componentes.add(checkLimite);

        componentes.add(confirmDeletarNo);
        componentes.add(confirmDeletarArvore);

    }

    //----------< Atualizar >----------//
    @Override
    public void update(double delta) {

        atualizarComponentes(delta);

        //Joystick (Movimento da Câmera)
        Color fundoBotao = LIGHTGRAY;
        Color cliqueBotao = new Color(151, 232, 255, 255);

        if (isKeyDown(KEY_UP) || btnCima.isMouseDown()) {
            cameraPos.y += cameraVel * delta;
            btnCima.setBackgroundColor(cliqueBotao);
        } else {
            btnCima.setBackgroundColor(fundoBotao);
        }

        if (isKeyDown(KEY_DOWN) || btnBaixo.isMouseDown()) {
            cameraPos.y -= cameraVel * delta;
            btnBaixo.setBackgroundColor(cliqueBotao);
        } else {
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

        //Zoom da Câmera
        if (getMouseWheelMove() < 0 || btnMenos.isMouseDown()) {
            camera.zoom -= 1 * delta;
            btnMenos.setBackgroundColor(cliqueBotao);
        } else {
            btnMenos.setBackgroundColor(fundoBotao);
        }

        if (getMouseWheelMove() > 0 || btnMais.isMouseDown()) {
            camera.zoom += 1 * delta;
            btnMais.setBackgroundColor(cliqueBotao);
        } else {
            btnMais.setBackgroundColor(fundoBotao);
        }

        //Resetar Câmera
        if (isKeyDown(KEY_R) || btnReset.isMousePressed()) {
            camera.rotation = 0;
            camera.zoom = 1;
            camera.target.x = 0;
            camera.target.y = 0;
            btnReset.setBackgroundColor(cliqueBotao);
        } else {
            btnReset.setBackgroundColor(fundoBotao);
        }

        //Atualizar Câmera
        camera.target.x = cameraPos.x;
        camera.target.y = cameraPos.y;

        //Animação dos Nós
        if (animando) {

            animTempo += delta * animVel;

            if (animTempo > 1) {
                animTempo = 1;
            }

            animPos.x = lerp(animIni.x, animFim.x, animTempo);
            animPos.y = lerp(animIni.y, animFim.y, animTempo);

            if (animTempo >= 1) {
                animando = false;
            }

        }

        //Limitação de Dígitos
        if (checkLimite.isSelected()) {
            if (textFieldValor.getValue().length() > 3) {
                textFieldValor.setValue(textFieldValor.getValue().substring(0, 3));
            }
        }

        //Atualizar Valores da Árvore
        if (!animando && textFieldValor.getValue() != "" && (btnCriar.isMousePressed() || isKeyDown(KEY_ENTER))) {

            btnCriar.setBackgroundColor(cliqueBotao);

            animValue = textFieldValor.getValue();
            animTempo = 0;

            if (dropdownTipoArvore.getSelectedItemIndex() == 0) {
                arvoreBB.put(Integer.valueOf(textFieldValor.getValue()), textFieldValor.getValue());
                nosBB = arvoreBB.coletarParaDesenho();

                for (var no : nosBB) {
                    if (Objects.equals(no.key, Integer.valueOf(animValue))) {
                        animFim = new Vector2(espacamento * no.ranque + margemEsquerda, espacamento * no.nivel + margemCima);

                        if (no.ranque == ranqueAnterior) {
                            animIni = new Vector2(espacamento * (no.ranque + 1) + margemEsquerda, espacamento * (no.nivel - 1) + margemCima);
                        }

                        ranqueAnterior = no.ranque;

                    }
                }

            } else if (dropdownTipoArvore.getSelectedItemIndex() == 1) {
                arvoreAVL.put(Integer.valueOf(textFieldValor.getValue()), textFieldValor.getValue());
                nosAVL = arvoreAVL.coletarParaDesenho();

                for (var no : nosAVL) {
                    if (Objects.equals(no.key, Integer.valueOf(animValue))) {
                        animFim = new Vector2(espacamento * no.ranque + margemEsquerda, espacamento * no.nivel + margemCima);

                        if (no.ranque == ranqueAnterior) {
                            animIni = new Vector2(espacamento * (no.ranque + 1) + margemEsquerda, espacamento * (no.nivel - 1) + margemCima);
                        } else if (no.nivel == nivelAnterior) {
                            animIni = new Vector2(espacamento * (no.ranque - 1) + margemEsquerda, espacamento * (no.nivel - 1) + margemCima);
                        }

                        ranqueAnterior = no.ranque;
                        nivelAnterior = no.nivel;

                    }
                }

            } else {
                arvoreVP.put(Integer.valueOf(textFieldValor.getValue()), textFieldValor.getValue());
                nosVP = arvoreVP.coletarParaDesenho();

                for (var no : nosVP) {
                    if (Objects.equals(no.key, Integer.valueOf(animValue))) {
                        animFim = new Vector2(espacamento * no.ranque + margemEsquerda, espacamento * no.nivel + margemCima);

                        if (no.ranque == ranqueAnterior) {
                            animIni = new Vector2(espacamento * (no.ranque + 1) + margemEsquerda, espacamento * (no.nivel - 1) + margemCima);
                            igualDeNovo = false;
                        } else if (igualDeNovo) {
                            animIni = new Vector2(espacamento * (no.ranque - 1) + margemEsquerda, espacamento * (no.nivel - 1) + margemCima);
                            igualDeNovo = false;
                        } else if (no.nivel == nivelAnterior) {
                            animIni = new Vector2(espacamento * (no.ranque - 1) + margemEsquerda, espacamento * (no.nivel + 1) + margemCima);
                            igualDeNovo = true;
                        }

                        ranqueAnterior = no.ranque;
                        nivelAnterior = no.nivel;

                        System.out.println("R: " + ranqueAnterior + " N: " + nivelAnterior + "B: " + igualDeNovo);

                    }
                }

            }

            animPos = animIni;
            animando = true;

        } else {
            btnCriar.setBackgroundColor(fundoBotao);
        }

        //Deletar a Árvore Inteira
        if (btnLimpar.isMousePressed() || isKeyDown(KEY_DELETE)) {
            valorAnterior = textFieldValor.getValue();
            textFieldValor.setValue("");
            confirmDeletarArvore.show();
        }

        if (confirmDeletarArvore.isVisible() && (confirmDeletarArvore.isButton1Pressed() || isKeyDown(KEY_ENTER))) {

            switch (dropdownTipoArvore.getSelectedItemIndex()) {
                case 0:
                    arvoreBB.clear();
                    nosBB = arvoreBB.coletarParaDesenho();
                    ranqueAnterior = -1;
                    animIni = new Vector2(margemEsquerda, margemCima);
                    break;
                case 1:
                    arvoreAVL.clear();
                    nosAVL = arvoreAVL.coletarParaDesenho();
                    ranqueAnterior = -1;
                    animIni = new Vector2(margemEsquerda, margemCima);
                    break;
                case 2:
                    arvoreVP.clear();
                    nosVP = arvoreVP.coletarParaDesenho();
                    ranqueAnterior = -1;
                    animIni = new Vector2(margemEsquerda, margemCima);
                    break;
            }

            camera.rotation = 0;
            camera.zoom = 1;
            camera.target.x = 0;
            camera.target.y = 0;

            confirmDeletarArvore.hide();
            animIni = new Vector2(margemEsquerda, margemCima);

            textFieldValor.setValue(valorAnterior);

        } else if (confirmDeletarArvore.isButton2Pressed() || confirmDeletarArvore.isCloseButtonPressed()) {
            confirmDeletarArvore.hide();
            textFieldValor.setValue(valorAnterior);
        }

        //Resetar os nós ao mudar o Tipo de Árvore
        if (dropdownTipoArvore.getSelectedItemIndex() != indexAnteriorDropdown) {

            if (!arvoreBB.isEmpty()) {
                arvoreBB.clear();
                nosBB = arvoreBB.coletarParaDesenho();
                ranqueAnterior = -1;
                animIni = new Vector2(margemEsquerda, margemCima);
            } else if (!arvoreAVL.isEmpty()) {
                arvoreAVL.clear();
                nosAVL = arvoreAVL.coletarParaDesenho();
                ranqueAnterior = -1;
                animIni = new Vector2(margemEsquerda, margemCima);
            } else if (!arvoreVP.isEmpty()) {
                arvoreVP.clear();
                nosVP = arvoreVP.coletarParaDesenho();
                ranqueAnterior = -1;
                animIni = new Vector2(margemEsquerda, margemCima);
            }

            indexAnteriorDropdown = dropdownTipoArvore.getSelectedItemIndex();

        }

        //Janela de Confirmação de Remoção do Nó
        if (confirmDeletarNo.isButton1Pressed()) {

            switch (dropdownTipoArvore.getSelectedItemIndex()) {
                case 0:
                    arvoreBB.delete(noAtual);
                    nosBB = arvoreBB.coletarParaDesenho();
                    break;
                case 1:
                    arvoreAVL.delete(noAtual);
                    nosAVL = arvoreAVL.coletarParaDesenho();
                    break;
                case 2:
                    arvoreVP.delete(noAtual);
                    nosVP = arvoreVP.coletarParaDesenho();
                    break;
            }

            confirmDeletarNo.hide();

        } else if (confirmDeletarNo.isButton2Pressed() || confirmDeletarNo.isCloseButtonPressed()) {
            confirmDeletarNo.hide();
        }

        //Remoção dos Nós da Árvore
        Vector2 mousePos = camera.getScreenToWorld(getMousePositionPoint()); //Aqui precisamos converter a posição do mouse no mundo real, para a posição dele com a interferência da câmera

        if (isMouseButtonPressed(MOUSE_BUTTON_LEFT)) {

            if (dropdownTipoArvore.getSelectedItemIndex() == 0) {

                for (ArvoreBinariaBusca.Node<Integer, String> no : nosBB) {
                    Vector2 centro = new Vector2(
                            espacamento * no.ranque + margemEsquerda,
                            espacamento * no.nivel + margemCima
                    );

                    if (CollisionUtils.checkCollisionPointCircle(mousePos, centro, raio)) {
                        confirmDeletarNo.setText("Nó: " + no.key + " | Ranque: " + no.ranque + " | Nível: " + no.nivel);
                        noAtual = no.key;
                        confirmDeletarNo.show();
                    }
                }

            } else if (dropdownTipoArvore.getSelectedItemIndex() == 1) {

                for (ArvoreAVL.Node<Integer, String> no : nosAVL) {
                    Vector2 centro = new Vector2(
                            espacamento * no.ranque + margemEsquerda,
                            espacamento * no.nivel + margemCima
                    );

                    if (CollisionUtils.checkCollisionPointCircle(mousePos, centro, raio)) {
                        confirmDeletarNo.setText("Nó: " + no.key + " | Ranque: " + no.ranque + " | Nível: " + no.nivel);
                        noAtual = no.key;
                        confirmDeletarNo.show();
                    }
                }

            } else {
                for (ArvoreVermelhoPreto.Node<Integer, String> no : nosVP) {
                    Vector2 centro = new Vector2(
                            espacamento * no.ranque + margemEsquerda,
                            espacamento * no.nivel + margemCima
                    );

                    if (CollisionUtils.checkCollisionPointCircle(mousePos, centro, raio)) {
                        confirmDeletarNo.setText("Nó: " + no.key + " | Ranque: " + no.ranque + " | Nível: " + no.nivel);
                        noAtual = no.key;
                        confirmDeletarNo.show();
                    }
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

        switch (dropdownTipoArvore.getSelectedItemIndex()) {
            case 0:
                for (ArvoreBinariaBusca.Node<Integer, String> no : nosBB) {
                    desenharNo(no, espacamento, espacamento);
                }
                break;
            case 1:
                for (ArvoreAVL.Node<Integer, String> no : nosAVL) {
                    desenharNo(no, espacamento, espacamento);
                }
                break;
            case 2:
                for (ArvoreVermelhoPreto.Node<Integer, String> no : nosVP) {
                    desenharNo(no, espacamento, espacamento);
                }
                break;
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
        fillCircle(1063, 125, 70, WHITE);
        drawCircle(1063, 125, 70, BLACK);
        fillCircle(1168, 125, 25, WHITE);
        drawCircle(1168, 125, 25, BLACK);
        fillCircle(958, 125, 25, WHITE);
        drawCircle(958, 125, 25, BLACK);

        //Quadro para Animação
        fillRectangle(960, 275, 200, 125, WHITE);
        drawRectangle(960, 275, 200, 125, BLACK);

        beginMode2D(camera);
        if (animando) {

            int numDigitos = String.valueOf(animValue).length();

            fillCircle(animPos.x, animPos.y, 20, GREEN);
            switch (numDigitos) {
                case 1 ->
                    drawText(animValue, animPos.x - 4, animPos.y - 5, 15, BLACK);
                case 2 ->
                    drawText(animValue, animPos.x - 9, animPos.y - 5, 15, BLACK);
                default ->
                    drawText(animValue, animPos.x - 14, animPos.y - 5, 15, BLACK);
            }
            drawCircle(animPos.x, animPos.y, 20, BLACK);

        }
        endMode2D();

        //Limitação de Digitos
        fillRectangle(960, 470, 200, 40, WHITE);
        drawRectangle(960, 470, 200, 40, BLACK);
        if (checkLimite.isSelected()) {
            drawText("*máximo de 3 dígitos", 995, 520, 12, RED);
        }

        //Textos do Projeto
        switch (dropdownTipoArvore.getSelectedItemIndex()) {
            case 0 ->
                drawOutlinedText("Árvore Binária de Busca", 50, 50, 20, BEIGE, 1, BLACK);
            case 1 ->
                drawOutlinedText("Árvore AVL", 50, 50, 20, BEIGE, 1, BLACK);
            case 2 ->
                drawOutlinedText("Árvore Vermelho e Preto", 50, 50, 20, BEIGE, 1, BLACK);
        }

        desenharComponentes();

    }

    //----------< Complementares >----------//    
    public void atualizarComponentes(double delta) {

        for (GuiComponent c : componentes) {
            if (!componentes.isEmpty()) {
                c.update(delta);
            }
        }

    }

    public void desenharComponentes() {

        for (GuiComponent c : componentes) {
            if (!componentes.isEmpty()) {
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

    private void desenharNo(ArvoreBinariaBusca.Node<Integer, String> no, int espHorizontal, int espVertical) {

        int x = espHorizontal * no.ranque + margemEsquerda;
        int y = espVertical * no.nivel + margemCima;

        int numDigitos = String.valueOf(no.key).length();

        fillCircle(x, y, raio, no.cor);
        switch (numDigitos) {
            case 1 ->
                drawText(no.value, x - 4, y - 5, 15, BLACK);
            case 2 ->
                drawText(no.value, x - 9, y - 5, 15, BLACK);
            default ->
                drawText(no.value, x - 14, y - 5, 15, BLACK);
        }
        drawCircle(x, y, raio, BLACK);
    }

    private void desenharNo(ArvoreAVL.Node<Integer, String> no, int espHorizontal, int espVertical) {

        int x = espHorizontal * no.ranque + margemEsquerda;
        int y = espVertical * no.nivel + margemCima;

        int numDigitos = String.valueOf(no.key).length();

        fillCircle(x, y, raio, no.cor);
        switch (numDigitos) {
            case 1 ->
                drawText(no.value, x - 4, y - 5, 15, BLACK);
            case 2 ->
                drawText(no.value, x - 9, y - 5, 15, BLACK);
            default ->
                drawText(no.value, x - 14, y - 5, 15, BLACK);
        }
        drawCircle(x, y, raio, BLACK);
    }

    private void desenharNo(ArvoreVermelhoPreto.Node<Integer, String> no, int espHorizontal, int espVertical) {

        int x = espHorizontal * no.ranque + margemEsquerda;
        int y = espVertical * no.nivel + margemCima;

        int numDigitos = String.valueOf(no.key).length();

        fillCircle(x, y, raio, no.cor);
        switch (numDigitos) {
            case 1 ->
                drawText(no.value, x - 4, y - 5, 15, BLACK);
            case 2 ->
                drawText(no.value, x - 9, y - 5, 15, BLACK);
            default ->
                drawText(no.value, x - 14, y - 5, 15, BLACK);
        }
        drawCircle(x, y, raio, BLACK);
    }

    //----------< Instanciar Engine e Iniciá-la >----------//
    public static void main(String[] args) {
        new Main();
    }

}
