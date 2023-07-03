package com.igrs.cleardata.utils;

import static android.view.KeyEvent.*;

import android.os.Build;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class KeyCodeHelper {

    private static final byte LEFT_CONTROL_PRESSED = 1;
    private static final byte LEFT_SHIFT_PRESSED = 1 << 1;
    private static final byte LEFT_ALT_PRESSED = 1 << 2;
    private static final byte LEFT_GUI_WINDOWS_PRESSED = 1 << 3;
    private static final byte RIGHT_CONTROL_PRESSED = 1 << 4;
    private static final byte RIGHT_SHIFT_PRESSED = 1 << 5;
    private static final byte RIGHT_ALT_PRESSED = 1 << 6;
    private static final byte RIGHT_GUI_WINDOWS_PRESSED = (byte)(1 << 7);

    private static final byte KEY_A = 0X4;
    private static final byte KEY_B = 0X5;
    private static final byte KEY_C = 0X6;
    private static final byte KEY_D = 0X7;
    private static final byte KEY_E = 0X8;
    private static final byte KEY_F = 0X9;
    private static final byte KEY_G = 0XA;
    private static final byte KEY_H = 0XB;
    private static final byte KEY_I = 0XC;
    private static final byte KEY_J = 0XD;
    private static final byte KEY_K = 0XE;
    private static final byte KEY_L = 0XF;
    private static final byte KEY_M = 0X10;
    private static final byte KEY_N = 0X11;
    private static final byte KEY_O = 0X12;
    private static final byte KEY_P = 0X13;
    private static final byte KEY_Q = 0X14;
    private static final byte KEY_R = 0X15;
    private static final byte KEY_S = 0X16;
    private static final byte KEY_T = 0X17;
    private static final byte KEY_U = 0X18;
    private static final byte KEY_V = 0X19;
    private static final byte KEY_W = 0X1A;
    private static final byte KEY_X = 0X1B;
    private static final byte KEY_Y = 0X1C;
    private static final byte KEY_Z = 0X1D;
    private static final byte KEY_1 = 0X1E;
    private static final byte KEY_2 = 0X1F;
    private static final byte KEY_3 = 0X20;
    private static final byte KEY_4 = 0X21;
    private static final byte KEY_5 = 0X22;
    private static final byte KEY_6 = 0X23;
    private static final byte KEY_7 = 0X24;
    private static final byte KEY_8 = 0X25;
    private static final byte KEY_9 = 0X26;
    private static final byte KEY_0 = 0X27;
    private static final byte KEY_RETURN = 0X28;
    private static final byte KEY_ESC = 0X29;
    private static final byte KEY_DEL = 0X2A;
    private static final byte KEY_TAB = 0X2B;
    private static final byte KEY_SPACE = 0X2C;
    private static final byte KEY_DASH = 0X2D; // -
    private static final byte KEY_EQUAL = 0X2E; // =
    private static final byte KEY_LEFT_SQUARE = 0X2F; // [
    private static final byte KEY_RIGHT_SQUARE = 0X30; // ]
    private static final byte KEY_SLASH = 0X31; // \
    private static final byte KEY_POUND_SIGN = 0X32; // #
    private static final byte KEY_SEMICOLON = 0X33; // ;
    private static final byte KEY_SINGLE_QUOT = 0X34;// '
    private static final byte KEY_GRAVE = 0X35; // DONT KNOW
    private static final byte KEY_COMMA = 0X36; // ,
    private static final byte KEY_DOT = 0X37; // .
    private static final byte KEY_LEFT_SLASH = 0X38; // /
    private static final byte KEY_CAPS_LOCK = 0X39;
    private static final byte KEY_F1 = 0X3A;
    private static final byte KEY_F2 = 0X3B;
    private static final byte KEY_F3 = 0X3C;
    private static final byte KEY_F4 = 0X3D;
    private static final byte KEY_F5 = 0X3E;
    private static final byte KEY_F6 = 0X3F;
    private static final byte KEY_F7 = 0X40;
    private static final byte KEY_F8 = 0X41;
    private static final byte KEY_F9 = 0X42;
    private static final byte KEY_F10 = 0X43;
    private static final byte KEY_F11 = 0X44;
    private static final byte KEY_F12 = 0X45;
    private static final byte KEY_PRINT_SCREEN = 0X46;
    private static final byte KEY_SCROLL_LOCK = 0X47;
    private static final byte KEY_PAUSE = 0X48;
    private static final byte KEY_INSERT = 0X49;
    private static final byte KEY_HOME = 0X4A;
    private static final byte KEY_PAGE_UP = 0X4B;
    private static final byte KEY_DELETE_FORWARD = 0X4C;
    private static final byte KEY_END = 0X4D;
    private static final byte KEY_PAGE_DOWN = 0X4E;
    private static final byte KEY_RIGHT_ARROW = 0X4F;
    private static final byte KEY_LEFT_ARROW = 0X50;
    private static final byte KEY_DOWN_ARROW = 0X51;
    private static final byte KEY_UP_ARROW = 0X52;
    private static final byte KEY_NUM_LOCK = 0X53;
    private static final byte KEY_PAD_DIVIDE = 0X54;
    private static final byte KEY_PAD_MULTIPLE = 0X55;
    private static final byte KEY_PAD_MINUS = 0X56;
    private static final byte KEY_PAD_PLUS = 0X57;
    private static final byte KEY_ENTER = 0X58;
    private static final byte KEY_PAD_1 = 0X59;
    private static final byte KEY_PAD_2 = 0X5A;
    private static final byte KEY_PAD_3 = 0X5B;
    private static final byte KEY_PAD_4 = 0X5C;
    private static final byte KEY_PAD_5 = 0X5D;
    private static final byte KEY_PAD_6 = 0X5E;
    private static final byte KEY_PAD_7 = 0X5F;
    private static final byte KEY_PAD_8 = 0X60;
    private static final byte KEY_PAD_9 = 0X61;
    private static final byte KEY_PAD_0 = 0X62;
    private static final byte KEY_PAD_DOT = 0X63;
    private static final byte KEY_NON_US_RIGHT_SLASH = 0X64; // \
    private static final byte KEY_APPLICATION = 0X65;
    private static final byte KEY_POWER = 0X66;
    private static final byte KEY_PAD_EQUAL = 0X67;
    private static final byte KEY_F13 = 0X68;
    private static final byte KEY_F14 = 0X69;
    private static final byte KEY_F15 = 0X6A;
    private static final byte KEY_F16 = 0X6B;
    private static final byte KEY_F17 = 0X6C;
    private static final byte KEY_F18 = 0X6D;
    private static final byte KEY_F19 = 0X6E;
    private static final byte KEY_F20 = 0X6F;
    private static final byte KEY_F21 = 0X70;
    private static final byte KEY_F22 = 0X71;
    private static final byte KEY_F23 = 0X72;
    private static final byte KEY_F24 = 0X73;
    private static final byte KEY_EXECUTE = 0X74;
    private static final byte KEY_HELP = 0X75;
    private static final byte KEY_MENU = 0X76;
    private static final byte KEY_SELECT = 0X77;
    private static final byte KEY_STOP = 0X78;
    private static final byte KEY_AGAIN = 0X79;
    private static final byte KEY_UNDO = 0X7A;
    private static final byte KEY_CUT = 0X7B;
    private static final byte KEY_COPY = 0X7C;
    private static final byte KEY_PASTE = 0X7D;
    private static final byte KEY_FIND = 0X7E;
    private static final byte KEY_MUTE = 0X7F;
    private static final byte KEY_VOLUME_UP = (byte) 0X80;
    private static final byte KEY_VOLUME_DOWN = (byte) 0X81;
    private static final byte KEY_LOCK_CAP = (byte) 0X82;
    private static final byte KEY_LOCK_NUMBER = (byte) 0X83;
    private static final byte KEY_LOCK_SCROLL = (byte) 0X84;
    private static final byte KEY_PAD_COMMA = (byte) 0X85;
    private static final byte KEY_PAD_EQUAL_SIGN = (byte) 0X86;
    private static final byte KEY_106 = (byte) 0X87;
    private static final byte KEY_107 = (byte) 0X88;
    private static final byte KEY_108 = (byte) 0X89;
    private static final byte KEY_109 = (byte) 0X8A;
    private static final byte KEY_110 = (byte) 0X8B;
    private static final byte KEY_111 = (byte) 0X8C;
    private static final byte KEY_112 = (byte) 0X8D;
    private static final byte KEY_113 = (byte) 0X8E;
    private static final byte KEY_114 = (byte) 0X8F;
    private static final byte KEY_115 = (byte) 0X90;
    private static final byte KEY_116 = (byte) 0X91;
    private static final byte KEY_117 = (byte) 0X92;
    private static final byte KEY_118 = (byte) 0X93;
    private static final byte KEY_119 = (byte) 0X94;
    private static final byte KEY_120 = (byte) 0X95;
    private static final byte KEY_121 = (byte) 0X96;
    private static final byte KEY_122 = (byte) 0X97;
    private static final byte KEY_123 = (byte) 0X98;
    private static final byte KEY_124 = (byte) 0X99;
    private static final byte KEY_125 = (byte) 0X9A;
    private static final byte KEY_CANCEL = (byte) 0X9B;
    private static final byte KEY_CLEAR = (byte) 0X9C;
    private static final byte KEY_PRIOR = (byte) 0X9D;
    private static final byte KEY_RETURN_KEY = (byte) 0X9E;
    private static final byte KEY_SEPARATOR = (byte) 0X9F;
    private static final byte KEY_OUT = (byte) 0XA0;
    private static final byte KEY_OPER = (byte) 0XA1;
    private static final byte KEY_CLEAR_AGAIN = (byte) 0XA2;
    private static final byte KEY_CRSEL = (byte) 0XA3;
    private static final byte KEY_EXSEL = (byte) 0XA4;
    private static final byte KEY_136 = (byte) 0XA5;
    private static final byte KEY_137 = (byte) 0XA6;
    private static final byte KEY_138 = (byte) 0XA7;
    private static final byte KEY_139 = (byte) 0XA8;
    private static final byte KEY_140 = (byte) 0XA9;
    private static final byte KEY_141 = (byte) 0XAA;
    private static final byte KEY_142 = (byte) 0XAB;
    private static final byte KEY_143 = (byte) 0XAC;
    private static final byte KEY_144 = (byte) 0XAD;
    private static final byte KEY_145 = (byte) 0XAE;
    private static final byte KEY_146 = (byte) 0XAF;
    private static final byte KEY_PAD_00 = (byte) 0XB0;
    private static final byte KEY_PAD_000 = (byte) 0XB1;
    private static final byte KEY_PAD_THOUSANDS = (byte) 0XB2;
    private static final byte KEY_DECIMAL = (byte) 0XB3;
    private static final byte KEY_CURRENCY = (byte) 0XB4;
    private static final byte KEY_CURRENCY_SUB = (byte) 0XB5;
    private static final byte KEY_OPEN_PARENTHESIS = (byte) 0XB6; // (
    private static final byte KEY_CLOSE_PARENTHESIS = (byte) 0XB7; // )
    private static final byte KEY_OPEN_CURLY = (byte) 0XB8; // {
    private static final byte KEY_CLOSE_CURLY = (byte) 0XB9; // }
    private static final byte KEY_PAD_TAB = (byte) 0XBA;
    private static final byte KEY_PAD_BACKSPACE = (byte) 0XBB;
    private static final byte KEY_PAD_A = (byte) 0XBC;
    private static final byte KEY_PAD_B = (byte) 0XBD;
    private static final byte KEY_PAD_C = (byte) 0XBE;
    private static final byte KEY_PAD_D = (byte) 0XBF;
    private static final byte KEY_PAD_E = (byte) 0XC0;
    private static final byte KEY_PAD_F = (byte) 0XC1;
    private static final byte KEY_PAD_XOR = (byte) 0XC2;
    private static final byte KEY_PAD_INDEX = (byte) 0XC3; // ^
    private static final byte KEY_PAD_MOD = (byte) 0XC4; // %
    private static final byte KEY_PAD_LESS_THAN = (byte) 0XC5; // <
    private static final byte KEY_PAD_GREATER_THAN = (byte) 0XC6; // >
    private static final byte KEY_PAD_BIT_AND = (byte) 0XC7; // &
    private static final byte KEY_PAD_AND = (byte) 0XC8; // &&
    private static final byte KEY_PAD_BIT_OR = (byte) 0XC9; // |
    private static final byte KEY_PAD_OR = (byte) 0XCA; // ||
    private static final byte KEY_PAD_COLON = (byte) 0XCB; // :
    private static final byte KEY_PAD_DASH = (byte) 0XCC; // #
    private static final byte KEY_PAD_SPACE = (byte) 0XCD;
    private static final byte KEY_PAD_AT = (byte) 0XCE; // @
    private static final byte KEY_EXCLAMATION = (byte) 0XCF; // !
    private static final byte KEY_179 = (byte) 0XD0;
    private static final byte KEY_180 = (byte) 0XD1;
    private static final byte KEY_181 = (byte) 0XD2;
    private static final byte KEY_182 = (byte) 0XD3;
    private static final byte KEY_183 = (byte) 0XD4;
    private static final byte KEY_184 = (byte) 0XD5;
    private static final byte KEY_185 = (byte) 0XD6;
    private static final byte KEY_186 = (byte) 0XD7;
    private static final byte KEY_187 = (byte) 0XD8;
    private static final byte KEY_188 = (byte) 0XD9;
    private static final byte KEY_189 = (byte) 0XDA;
    private static final byte KEY_190 = (byte) 0XDB;
    private static final byte KEY_191 = (byte) 0XDC;
    private static final byte KEY_192 = (byte) 0XDD;
    private static final byte KEY_193 = (byte) 0XDE;
    private static final byte KEY_194 = (byte) 0XDF;
    private static final byte KEY_LEFT_CTRL = (byte) 0XE0;
    private static final byte KEY_LEFT_SHIFT = (byte) 0XE1;
    private static final byte KEY_LEFT_ALT = (byte) 0XE2;
    private static final byte KEY_LEFT_GUI = (byte) 0XE3;
    private static final byte KEY_RIGHT_CTRL = (byte) 0XE4;
    private static final byte KEY_RIGHT_SHIFT = (byte) 0XE4;
    private static final byte KEY_RIGHT_ALT = (byte) 0XE4;
    private static final byte KEY_RIGHT_GUI = (byte) 0XE4;


    private static final byte[] KEY_CODE_MAP = new byte[1024];

    static {
        //KEY_CODE_MAP 要填充的数组    val 的所有元素中的值
        //KEY_CODE_MAP是一个数组变量，(byte)0 是一个val中元素数据类型的值，作用：填充KEY_CODE_MAP数组中的每个元素都是(byte)0
        Arrays.fill(KEY_CODE_MAP, (byte)0);

        KEY_CODE_MAP[KEYCODE_0] = KEY_0;
        KEY_CODE_MAP[KEYCODE_1] = KEY_1;
        KEY_CODE_MAP[KEYCODE_2] = KEY_2;
        KEY_CODE_MAP[KEYCODE_3] = KEY_3;
        KEY_CODE_MAP[KEYCODE_4] = KEY_4;
        KEY_CODE_MAP[KEYCODE_5] = KEY_5;
        KEY_CODE_MAP[KEYCODE_6] = KEY_6;
        KEY_CODE_MAP[KEYCODE_7] = KEY_7;
        KEY_CODE_MAP[KEYCODE_8] = KEY_8;
        KEY_CODE_MAP[KEYCODE_9] = KEY_9;

        KEY_CODE_MAP[KEYCODE_A] = KEY_A;
        KEY_CODE_MAP[KEYCODE_B] = KEY_B;
        KEY_CODE_MAP[KEYCODE_C] = KEY_C;
        KEY_CODE_MAP[KEYCODE_D] = KEY_D;
        KEY_CODE_MAP[KEYCODE_E] = KEY_E;
        KEY_CODE_MAP[KEYCODE_F] = KEY_F;
        KEY_CODE_MAP[KEYCODE_G] = KEY_G;
        KEY_CODE_MAP[KEYCODE_H] = KEY_H;
        KEY_CODE_MAP[KEYCODE_I] = KEY_I;
        KEY_CODE_MAP[KEYCODE_J] = KEY_J;
        KEY_CODE_MAP[KEYCODE_K] = KEY_K;
        KEY_CODE_MAP[KEYCODE_L] = KEY_L;
        KEY_CODE_MAP[KEYCODE_M] = KEY_M;
        KEY_CODE_MAP[KEYCODE_N] = KEY_N;
        KEY_CODE_MAP[KEYCODE_O] = KEY_O;
        KEY_CODE_MAP[KEYCODE_P] = KEY_P;
        KEY_CODE_MAP[KEYCODE_Q] = KEY_Q;
        KEY_CODE_MAP[KEYCODE_R] = KEY_R;
        KEY_CODE_MAP[KEYCODE_S] = KEY_S;
        KEY_CODE_MAP[KEYCODE_T] = KEY_T;
        KEY_CODE_MAP[KEYCODE_U] = KEY_U;
        KEY_CODE_MAP[KEYCODE_V] = KEY_V;
        KEY_CODE_MAP[KEYCODE_W] = KEY_W;
        KEY_CODE_MAP[KEYCODE_X] = KEY_X;
        KEY_CODE_MAP[KEYCODE_Y] = KEY_Y;
        KEY_CODE_MAP[KEYCODE_Z] = KEY_Z;

        KEY_CODE_MAP[KEYCODE_NUMPAD_0] = KEY_PAD_0;
        KEY_CODE_MAP[KEYCODE_NUMPAD_1] = KEY_PAD_1;
        KEY_CODE_MAP[KEYCODE_NUMPAD_2] = KEY_PAD_2;
        KEY_CODE_MAP[KEYCODE_NUMPAD_3] = KEY_PAD_3;
        KEY_CODE_MAP[KEYCODE_NUMPAD_4] = KEY_PAD_4;
        KEY_CODE_MAP[KEYCODE_NUMPAD_5] = KEY_PAD_5;
        KEY_CODE_MAP[KEYCODE_NUMPAD_6] = KEY_PAD_6;
        KEY_CODE_MAP[KEYCODE_NUMPAD_7] = KEY_PAD_7;
        KEY_CODE_MAP[KEYCODE_NUMPAD_8] = KEY_PAD_8;
        KEY_CODE_MAP[KEYCODE_NUMPAD_9] = KEY_PAD_9;
        KEY_CODE_MAP[KEYCODE_NUMPAD_MULTIPLY] = KEY_PAD_MULTIPLE;
        KEY_CODE_MAP[KEYCODE_NUMPAD_DIVIDE] = KEY_PAD_DIVIDE;
        KEY_CODE_MAP[KEYCODE_NUMPAD_ADD] = KEY_PAD_PLUS;
        KEY_CODE_MAP[KEYCODE_NUMPAD_SUBTRACT] = KEY_PAD_MINUS;
        KEY_CODE_MAP[KEYCODE_NUMPAD_EQUALS] = KEY_PAD_EQUAL;
        KEY_CODE_MAP[KEYCODE_NUMPAD_LEFT_PAREN] = KEY_OPEN_PARENTHESIS;
        KEY_CODE_MAP[KEYCODE_NUMPAD_RIGHT_PAREN] = KEY_CLOSE_PARENTHESIS;
        KEY_CODE_MAP[KEYCODE_NUMPAD_ENTER] = KEY_ENTER;

//        //alt
//        KEY_CODE_MAP[KEYCODE_ALT_LEFT] = KEY_LEFT_ALT;
//        KEY_CODE_MAP[KEYCODE_ALT_RIGHT] = KEY_RIGHT_ALT;
//
//        //ctrl
//        KEY_CODE_MAP[KEYCODE_CTRL_LEFT] = KEY_LEFT_CTRL;
//        KEY_CODE_MAP[KEYCODE_CTRL_RIGHT] = KEY_RIGHT_CTRL;
//
//        //shift
//        KEY_CODE_MAP[KEYCODE_SHIFT_LEFT] = KEY_LEFT_SHIFT;
//        KEY_CODE_MAP[KEYCODE_SHIFT_RIGHT] = KEY_RIGHT_SHIFT;

//        KEY_CODE_MAP[KEYCODE_WINDOW] = KEY_LEFT_GUI;

        KEY_CODE_MAP[KEYCODE_BACKSLASH] = KEY_SLASH;
        KEY_CODE_MAP[KEYCODE_SLASH] = KEY_LEFT_SLASH;

        KEY_CODE_MAP[KEYCODE_CAPS_LOCK] = KEY_CAPS_LOCK;
        KEY_CODE_MAP[KEYCODE_CLEAR] = KEY_CLEAR;
        KEY_CODE_MAP[KEYCODE_COMMA] = KEY_COMMA;
        KEY_CODE_MAP[KEYCODE_DEL] = KEY_DEL;

        // arrow
        KEY_CODE_MAP[KEYCODE_DPAD_DOWN] = KEY_DOWN_ARROW;
        KEY_CODE_MAP[KEYCODE_DPAD_LEFT] = KEY_LEFT_ARROW;
        KEY_CODE_MAP[KEYCODE_DPAD_RIGHT] = KEY_RIGHT_ARROW;
        KEY_CODE_MAP[KEYCODE_DPAD_UP] = KEY_UP_ARROW;
        KEY_CODE_MAP[KEYCODE_NUMPAD_DOT] = KEY_PAD_DOT;

        // Enter
        KEY_CODE_MAP[KEYCODE_ENTER] = KEY_ENTER;

        KEY_CODE_MAP[KEYCODE_EQUALS] = KEY_EQUAL;
        KEY_CODE_MAP[KEYCODE_ESCAPE] = KEY_ESC;

        KEY_CODE_MAP[KEYCODE_F1] = KEY_F1;
        KEY_CODE_MAP[KEYCODE_F2] = KEY_F2;
        KEY_CODE_MAP[KEYCODE_F3] = KEY_F3;
        KEY_CODE_MAP[KEYCODE_F4] = KEY_F4;
        KEY_CODE_MAP[KEYCODE_F5] = KEY_F5;
        KEY_CODE_MAP[KEYCODE_F6] = KEY_F6;
        KEY_CODE_MAP[KEYCODE_F7] = KEY_F7;
        KEY_CODE_MAP[KEYCODE_F8] = KEY_F8;
        KEY_CODE_MAP[KEYCODE_F9] = KEY_F9;
        KEY_CODE_MAP[KEYCODE_F10] = KEY_F10;
        KEY_CODE_MAP[KEYCODE_F11] = KEY_F11;
        KEY_CODE_MAP[KEYCODE_F12] = KEY_F12;

        KEY_CODE_MAP[KEYCODE_FORWARD_DEL] = KEY_DELETE_FORWARD;
        KEY_CODE_MAP[KEYCODE_GRAVE] = KEY_GRAVE;

        KEY_CODE_MAP[KEYCODE_HOME] = KEY_HOME;

        KEY_CODE_MAP[KEYCODE_INSERT] = KEY_INSERT;

        KEY_CODE_MAP[KEYCODE_LEFT_BRACKET] = KEY_LEFT_SQUARE;
        KEY_CODE_MAP[KEYCODE_RIGHT_BRACKET] = KEY_RIGHT_SQUARE;
        KEY_CODE_MAP[KEYCODE_MENU] = KEY_MENU;
        KEY_CODE_MAP[KEYCODE_MOVE_END] = KEY_END;
        KEY_CODE_MAP[KEYCODE_MOVE_HOME] = KEY_HOME;

        KEY_CODE_MAP[KEYCODE_MINUS] = KEY_DASH;
        KEY_CODE_MAP[KEYCODE_PLUS] = KEY_PAD_PLUS;
        KEY_CODE_MAP[KEYCODE_NUM_LOCK] = KEY_NUM_LOCK;
        KEY_CODE_MAP[KEYCODE_PAGE_DOWN] = KEY_PAGE_DOWN;
        KEY_CODE_MAP[KEYCODE_PAGE_UP] = KEY_PAGE_UP;
        KEY_CODE_MAP[KEYCODE_POWER] = KEY_POWER;
        KEY_CODE_MAP[KEYCODE_POUND] = KEY_POUND_SIGN;
        KEY_CODE_MAP[KEYCODE_PERIOD] = KEY_DOT;
        KEY_CODE_MAP[KEYCODE_SPACE] = KEY_SPACE;
        KEY_CODE_MAP[KEYCODE_SEMICOLON] = KEY_SEMICOLON;
        KEY_CODE_MAP[KEYCODE_TAB] = KEY_TAB;
        KEY_CODE_MAP[KEYCODE_APOSTROPHE] = KEY_SINGLE_QUOT;

        KEY_CODE_MAP[KEYCODE_VOLUME_UP] = KEY_VOLUME_UP;
        KEY_CODE_MAP[KEYCODE_VOLUME_DOWN] = KEY_VOLUME_DOWN;
        KEY_CODE_MAP[KEYCODE_VOLUME_MUTE] = KEY_MUTE;

        KEY_CODE_MAP[KEYCODE_BREAK] = KEY_PAUSE;
        KEY_CODE_MAP[KEYCODE_SCROLL_LOCK] = KEY_SCROLL_LOCK;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            KEY_CODE_MAP[KEYCODE_COPY] = KEY_COPY;
            KEY_CODE_MAP[KEYCODE_CUT] = KEY_CUT;
            KEY_CODE_MAP[KEYCODE_HELP] = KEY_HELP;
            KEY_CODE_MAP[KEYCODE_PASTE] = KEY_PASTE;
        }
    }
    //按下的键
    private ArrayList<Byte> pressedKeys = new ArrayList<>();
    private byte currentFunctionKey = 0;

    public @Nullable byte[] onKeyPressed(int keyCode) {
        return onKeyPressed(keyCode,null);
    }

    /**
     * 拿到转换后的值
     * @param keyCode
     * @return
     */
    public static @Nullable byte getKeyCodeForMap(int keyCode) {
        if (keyCode >= KEY_CODE_MAP.length) {
            return 0x00;
        }
        byte keyValue = KEY_CODE_MAP[keyCode];
        return keyValue;
    }


    /**
     * 按下按键
     * @param keyCode
     * @param event
     * @return
     */
    @Nullable byte[] onKeyPressed(int keyCode, KeyEvent event) {
        if (keyCode >= KEY_CODE_MAP.length) {
            return null;
        }
        byte[] result = new byte[8];
        Arrays.fill(result, (byte) 0);

        byte functionKeyValue = parseFunctionKey(keyCode);

        byte keyValue = KEY_CODE_MAP[keyCode];
        if (keyValue == 0 && functionKeyValue == 0) {
            return null;
        }

        if (keyValue != 0) {
            if (pressedKeys.contains(keyValue)) {
                return null;
            }
            pressedKeys.add(keyValue);
        }

        currentFunctionKey |= functionKeyValue;
        result[0] = currentFunctionKey;
        for (int i =0; i< pressedKeys.size() && i < result.length - 2; ++i) {
            result[2 + i] = pressedKeys.get(i);
        }

        return result;
    }
    public  @Nullable byte[] onKeyUp(int keyCode) {
        return onKeyUp(keyCode,null);
    }
    /**
     * 按键抬起
     * @param keyCode
     * @param event
     * @return
     */
    @Nullable byte[] onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode >= KEY_CODE_MAP.length) {
            return null;
        }
        byte[] result = new byte[8];
        Arrays.fill(result, (byte) 0);

        byte functionKeyValue = parseFunctionKey(keyCode);

        byte keyValue = KEY_CODE_MAP[keyCode];
        if (keyValue == 0 && functionKeyValue == 0) {
            return null;
        }
        if (keyValue != 0) {
            int removedIndex = -1;
            for(int i = 0; i < pressedKeys.size(); ++i) {
                if (pressedKeys.get(i) == keyValue) {
                    removedIndex = i;
                }
            }
            if (removedIndex >= 0) {
                pressedKeys.remove(removedIndex);
            }
        }

        currentFunctionKey &= ~functionKeyValue;
        result[0] = currentFunctionKey;

        for (int i =0; i< pressedKeys.size() && i < result.length - 2; ++i) {
            result[2 + i] = pressedKeys.get(i);
        }


        return result;
    }

    public void reset() {
        currentFunctionKey = 0;
        pressedKeys.clear();
    }

    private byte parseFunctionKey(int keyCode) {
        switch (keyCode) {
            case KEYCODE_ALT_LEFT:
                return LEFT_ALT_PRESSED;

            case KEYCODE_ALT_RIGHT:
                return RIGHT_ALT_PRESSED;

            case KEYCODE_CTRL_LEFT:
                return LEFT_CONTROL_PRESSED;

            case KEYCODE_CTRL_RIGHT:
                return RIGHT_CONTROL_PRESSED;

            case KEYCODE_SHIFT_LEFT:
                return LEFT_SHIFT_PRESSED;

            case KEYCODE_SHIFT_RIGHT:
                return RIGHT_SHIFT_PRESSED;

            default:
                return 0;
        }
    }

    private static byte isCtrlPressed(int keyCode, KeyEvent event) {
        return event.isShiftPressed()? LEFT_CONTROL_PRESSED: 0;
    }

    private static byte isShiftPressed(int keyCode, KeyEvent event) {
        return event.isShiftPressed()? LEFT_SHIFT_PRESSED: 0;
    }

    private static byte isAltPressed(int keyCode, KeyEvent event) {
        return event.isAltPressed()? LEFT_ALT_PRESSED: 0;
    }



}
