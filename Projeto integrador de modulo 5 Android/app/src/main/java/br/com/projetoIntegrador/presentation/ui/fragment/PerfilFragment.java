package br.com.projetoIntegrador.presentation.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.PacienteDto;
import br.com.projetoIntegrador.presentation.viewmodel.PacienteViewModel;


public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private static final String ARG_PACIENTE_ID = "pacienteId";

    private PacienteViewModel pacienteViewModel;
    private Long pacienteIdLogado;

    private TextInputEditText etNomeCompleto, etDataNascimento, etCpf, etRg, etEmail, etTelefone;
    private TextInputEditText etRua, etCidade, etEstado, etCep;
    private TextInputEditText etAlergias, etMedicamentos;
    private Button btnSalvarPerfil;

    private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static PerfilFragment newInstance(Long pacienteId) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PACIENTE_ID, pacienteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pacienteIdLogado = getArguments().getLong(ARG_PACIENTE_ID);
        }
        pacienteViewModel = new ViewModelProvider(this).get(PacienteViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        bindViews(view);

        etDataNascimento.setOnClickListener(v -> showDatePickerDialog());
        btnSalvarPerfil.setOnClickListener(v -> salvarPerfil());

        if (pacienteIdLogado != null && pacienteIdLogado > 0) {
            carregarDadosPerfil();
        } else {
            Toast.makeText(getContext(), "ID do paciente inválido.", Toast.LENGTH_LONG).show();
            // Considerar desabilitar campos ou fechar fragment
        }
        return view;
    }

    private void bindViews(View view) {
        etNomeCompleto = view.findViewById(R.id.etPerfilNomeCompleto);
        etDataNascimento = view.findViewById(R.id.etPerfilDataNascimento);
        etCpf = view.findViewById(R.id.etPerfilCpf);
        etRg = view.findViewById(R.id.etPerfilRg);
        etEmail = view.findViewById(R.id.etPerfilEmail);
        etTelefone = view.findViewById(R.id.etPerfilTelefone);
        etRua = view.findViewById(R.id.etPerfilRua);
        etCidade = view.findViewById(R.id.etPerfilCidade);
        etEstado = view.findViewById(R.id.etPerfilEstado);
        etCep = view.findViewById(R.id.etPerfilCep);
        etAlergias = view.findViewById(R.id.etPerfilAlergias);
        etMedicamentos = view.findViewById(R.id.etPerfilMedicamentos);
        btnSalvarPerfil = view.findViewById(R.id.btnSalvarPerfil);
    }

    private void carregarDadosPerfil() {
        pacienteViewModel.getPacienteById(pacienteIdLogado).observe(getViewLifecycleOwner(), paciente -> {
            if (paciente != null) {
                etNomeCompleto.setText(paciente.getFullName());
                if (paciente.getBirthDate() != null) {
                    try {
                        Date dateApi = sdfApi.parse(paciente.getBirthDate());
                        if (dateApi != null) {
                            etDataNascimento.setText(sdfDisplay.format(dateApi));
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Erro ao parsear data de nascimento da API", e);
                        etDataNascimento.setText(paciente.getBirthDate());
                    }
                }

                etCpf.setText(paciente.getCpf());
                etRg.setText(paciente.getRg());
                etEmail.setText(paciente.getEmail());
                etTelefone.setText(paciente.getPhone());
                etRua.setText(paciente.getAddressStreet());
                etCidade.setText(paciente.getAddressCity());
                etEstado.setText(paciente.getAddressState());
                etCep.setText(paciente.getAddressZip());

                if (paciente.getAllergies() != null) {
                    etAlergias.setText(TextUtils.join(", ", paciente.getAllergies()));
                } else {
                    etAlergias.setText("");
                }
                if (paciente.getMedications() != null) {
                    etMedicamentos.setText(TextUtils.join(", ", paciente.getMedications()));
                } else {
                    etMedicamentos.setText("");
                }
            } else {
                Toast.makeText(getContext(), "Falha ao carregar dados do perfil.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(etDataNascimento.getText())) {
            try {
                Date existingDate = sdfDisplay.parse(etDataNascimento.getText().toString());
                if (existingDate != null) {
                    calendar.setTime(existingDate);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Erro ao parsear data existente no campo", e);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    etDataNascimento.setText(sdfDisplay.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Não permite data futura
        datePickerDialog.show();
    }


    private void salvarPerfil() {
        if (TextUtils.isEmpty(etNomeCompleto.getText())) {
            etNomeCompleto.setError("Nome não pode ser vazio");
            return;
        }
        // Adicione mais validações aqui

        PacienteDto pacienteAtualizado = new PacienteDto();
        pacienteAtualizado.setId(pacienteIdLogado);
        pacienteAtualizado.setFullName(etNomeCompleto.getText().toString().trim());

        if (!TextUtils.isEmpty(etDataNascimento.getText())) {
            try {
                Date dateDisplay = sdfDisplay.parse(etDataNascimento.getText().toString());
                if (dateDisplay != null) {
                    pacienteAtualizado.setBirthDate(sdfApi.format(dateDisplay));
                }
            } catch (ParseException e) {
                Log.e(TAG, "Erro ao parsear data de nascimento para API", e);
                Toast.makeText(getContext(), "Formato de data inválido.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Definir como nulo ou enviar string vazia, dependendo da API.
            // Se for obrigatório, adicione validação.
            pacienteAtualizado.setBirthDate(null);
        }

        // CPF geralmente não é alterado, então pegamos o valor existente
        if(etCpf.getText() != null) pacienteAtualizado.setCpf(etCpf.getText().toString());

        pacienteAtualizado.setRg(etRg.getText() != null ? etRg.getText().toString().trim() : null);
        pacienteAtualizado.setEmail(etEmail.getText() != null ? etEmail.getText().toString().trim() : null);
        pacienteAtualizado.setPhone(etTelefone.getText() != null ? etTelefone.getText().toString().trim() : null);
        pacienteAtualizado.setAddressStreet(etRua.getText() != null ? etRua.getText().toString().trim() : null);
        pacienteAtualizado.setAddressCity(etCidade.getText() != null ? etCidade.getText().toString().trim() : null);
        pacienteAtualizado.setAddressState(etEstado.getText() != null ? etEstado.getText().toString().trim() : null);
        pacienteAtualizado.setAddressZip(etCep.getText() != null ? etCep.getText().toString().trim() : null);

        if (etAlergias.getText() != null && !TextUtils.isEmpty(etAlergias.getText().toString().trim())) {
            pacienteAtualizado.setAllergies(Arrays.asList(etAlergias.getText().toString().trim().split("\\s*,\\s*")));
        } else {
            pacienteAtualizado.setAllergies(new ArrayList<>());
        }
        if (etMedicamentos.getText() != null && !TextUtils.isEmpty(etMedicamentos.getText().toString().trim())) {
            pacienteAtualizado.setMedications(Arrays.asList(etMedicamentos.getText().toString().trim().split("\\s*,\\s*")));
        } else {
            pacienteAtualizado.setMedications(new ArrayList<>());
        }
        // Não estamos alterando a senha aqui. Se fosse, precisaria de campos adicionais.
        // pacienteAtualizado.setPassword(null); // Não envie a senha a menos que esteja sendo alterada

        pacienteViewModel.updatePaciente(pacienteIdLogado, pacienteAtualizado).observe(getViewLifecycleOwner(), pacienteDto -> {
            if (pacienteDto != null) {
                Toast.makeText(getContext(), "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Falha ao atualizar perfil.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}