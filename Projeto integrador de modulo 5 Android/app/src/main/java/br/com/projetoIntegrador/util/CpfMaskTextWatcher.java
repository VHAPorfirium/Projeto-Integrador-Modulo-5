package br.com.projetoIntegrador.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CpfMaskTextWatcher implements TextWatcher {

    private final EditText editText;
    private boolean isUpdating = false;
    private String oldText = "";

    // Formato do CPF: 000.000.000-00
    private static final String CPF_MASK = "###.###.###-##";

    public CpfMaskTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Não é necessário implementar
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isUpdating) {
            isUpdating = false;
            return;
        }

        String str = unmask(s.toString());
        String maskedStr = "";

        if (str.length() > 11) {
            str = str.substring(0, 11);
        }

        int i = 0;
        for (char m : CPF_MASK.toCharArray()) {
            if (m != '#' && str.length() > oldText.length()) {
                maskedStr += m;
                continue;
            }
            try {
                maskedStr += str.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }

        isUpdating = true;
        editText.setText(maskedStr);
        editText.setSelection(maskedStr.length());

        oldText = str;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Não é necessário implementar
    }

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }
}