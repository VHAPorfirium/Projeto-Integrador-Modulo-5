package br.com.projetoIntegrador.presentation.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.PacienteDto;
import br.com.projetoIntegrador.presentation.viewmodel.PacienteViewModel;


public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private static final String ARG_PACIENTE_ID = "pacienteId";

    private PacienteViewModel pacienteViewModel;
    private Long pacienteIdLogado;

    private TextView tvNomeCompleto, tvDataNascimento, tvCpf, tvRg, tvEmail, tvTelefone;
    private TextView tvRua, tvCidade, tvEstado, tvCep;
    private TextView tvAlergias, tvMedicamentos;
    private TextView tvErrorMessage; // Adicionando o TextView para mensagens de erro

    private final java.text.SimpleDateFormat sdfDisplay = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


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
            Log.d(TAG, "onCreate: Paciente ID recebido: " + pacienteIdLogado);
        }
        pacienteViewModel = new ViewModelProvider(this).get(PacienteViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        bindViews(view);

        // Adicionando um ID para o LinearLayout principal para poder esconder o conteúdo
        View contentLayout = view.findViewById(R.id.perfil_layout_content);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage); // Inicializa tvErrorMessage

        if (pacienteIdLogado != null && pacienteIdLogado > 0) {
            carregarDadosPerfil();
            // Garante que o conteúdo é visível e a mensagem de erro escondida no início
            if (contentLayout != null) contentLayout.setVisibility(View.VISIBLE);
            if (tvErrorMessage != null) tvErrorMessage.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "onCreateView: ID do paciente inválido ou não fornecido: " + pacienteIdLogado);
            Toast.makeText(getContext(), "Não foi possível carregar o perfil. ID do paciente inválido.", Toast.LENGTH_LONG).show();
            // Esconde o conteúdo principal e mostra a mensagem de erro
            if (contentLayout != null) contentLayout.setVisibility(View.GONE);
            if (tvErrorMessage != null) {
                tvErrorMessage.setText("Erro: ID do paciente não encontrado.");
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    private void bindViews(View view) {
        tvNomeCompleto = view.findViewById(R.id.etPerfilNomeCompleto);
        tvDataNascimento = view.findViewById(R.id.etPerfilDataNascimento);
        tvCpf = view.findViewById(R.id.etPerfilCpf);
        tvRg = view.findViewById(R.id.etPerfilRg);
        tvEmail = view.findViewById(R.id.etPerfilEmail);
        tvTelefone = view.findViewById(R.id.etPerfilTelefone);
        tvRua = view.findViewById(R.id.etPerfilRua);
        tvCidade = view.findViewById(R.id.etPerfilCidade);
        tvEstado = view.findViewById(R.id.etPerfilEstado);
        tvCep = view.findViewById(R.id.etPerfilCep);
        tvAlergias = view.findViewById(R.id.etPerfilAlergias);
        tvMedicamentos = view.findViewById(R.id.etPerfilMedicamentos);
    }

    private void carregarDadosPerfil() {
        Log.d(TAG, "carregarDadosPerfil: Iniciando carregamento para o paciente ID: " + pacienteIdLogado);
        pacienteViewModel.getPacienteById(pacienteIdLogado).observe(getViewLifecycleOwner(), paciente -> {
            if (paciente != null) {
                Log.d(TAG, "carregarDadosPerfil: Paciente DTO recebido: " + paciente.getFullName());
                tvNomeCompleto.setText(getTextoOuPadrao(paciente.getFullName()));
                tvDataNascimento.setText(formatarBirthDateParaExibicao(paciente.getBirthDate()));
                tvCpf.setText(getTextoOuPadrao(paciente.getCpf()));
                tvRg.setText(getTextoOuPadrao(paciente.getRg()));
                tvEmail.setText(getTextoOuPadrao(paciente.getEmail()));
                tvTelefone.setText(getTextoOuPadrao(paciente.getPhone()));
                tvRua.setText(getTextoOuPadrao(paciente.getAddressStreet()));
                tvCidade.setText(getTextoOuPadrao(paciente.getAddressCity()));

                String estadoExibicao = (paciente.getAddressState() != null && !paciente.getAddressState().trim().isEmpty())
                        ? " - " + paciente.getAddressState()
                        : "";
                tvEstado.setText(estadoExibicao);
                tvCep.setText(getTextoOuPadrao(paciente.getAddressZip()));

                String alergias = (paciente.getAllergies() != null && !paciente.getAllergies().isEmpty())
                        ? TextUtils.join(", ", paciente.getAllergies())
                        : "Nenhuma informada";
                tvAlergias.setText(alergias);
                Log.d(TAG, "carregarDadosPerfil: Alergias: " + alergias);

                String medicamentos = (paciente.getMedications() != null && !paciente.getMedications().isEmpty())
                        ? TextUtils.join(", ", paciente.getMedications())
                        : "Nenhum informado";
                tvMedicamentos.setText(medicamentos);
                Log.d(TAG, "carregarDadosPerfil: Medicamentos: " + medicamentos);

                // Garante que o conteúdo seja visível e a mensagem de erro escondida se os dados forem carregados com sucesso
                View contentLayout = requireView().findViewById(R.id.perfil_layout_content);
                if (contentLayout != null) contentLayout.setVisibility(View.VISIBLE);
                if (tvErrorMessage != null) tvErrorMessage.setVisibility(View.GONE);

            } else {
                Log.e(TAG, "carregarDadosPerfil: Falha ao carregar paciente (DTO nulo).");
                Toast.makeText(getContext(), "Falha ao carregar dados do perfil.", Toast.LENGTH_SHORT).show();
                // Esconde o conteúdo e mostra a mensagem de erro se o carregamento falhar
                View contentLayout = requireView().findViewById(R.id.perfil_layout_content);
                if (contentLayout != null) contentLayout.setVisibility(View.GONE);
                if (tvErrorMessage != null) {
                    tvErrorMessage.setText("Não foi possível carregar os dados do perfil.");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String getTextoOuPadrao(String texto) {
        return (!TextUtils.isEmpty(texto) && !texto.trim().equals("null")) ? texto.trim() : "Não informado";
    }

    private String formatarBirthDateParaExibicao(List<Integer> birthDateList) {
        if (birthDateList == null || birthDateList.size() != 3) {
            return "Não informada";
        }
        try {
            int year = birthDateList.get(0);
            int month = birthDateList.get(1) - 1;
            int day = birthDateList.get(2);

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            return sdfDisplay.format(cal.getTime());
        } catch (Exception e) {
            Log.e(TAG, "Erro ao formatar birthDate (List<Integer>): " + birthDateList, e);
        }
        return "Data inválida";
    }
}