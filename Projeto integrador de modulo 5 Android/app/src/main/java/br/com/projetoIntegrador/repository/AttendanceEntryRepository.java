package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;

public class AttendanceEntryRepository {

    private ApiService apiService;

    public AttendanceEntryRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<List<AttendanceEntryDto>> listAllAttendanceEntries() {
        MutableLiveData<List<AttendanceEntryDto>> result = new MutableLiveData<>();
        apiService.listAllAttendanceEntries().enqueue(new Callback<List<AttendanceEntryDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AttendanceEntryDto>> call, @NonNull Response<List<AttendanceEntryDto>> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<List<AttendanceEntryDto>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<AttendanceEntryDto> getAttendanceEntryById(Long entryId) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.getAttendanceEntryById(entryId).enqueue(new Callback<AttendanceEntryDto>() {
            @Override
            public void onResponse(@NonNull Call<AttendanceEntryDto> call, @NonNull Response<AttendanceEntryDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<AttendanceEntryDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<AttendanceEntryDto> createAttendanceEntry(AttendanceEntryDto attendanceEntryDto) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.createAttendanceEntry(attendanceEntryDto).enqueue(new Callback<AttendanceEntryDto>() {
            @Override
            public void onResponse(@NonNull Call<AttendanceEntryDto> call, @NonNull Response<AttendanceEntryDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<AttendanceEntryDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<AttendanceEntryDto> updateAttendanceEntry(Long entryId, AttendanceEntryDto attendanceEntryDto) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.updateAttendanceEntry(entryId, attendanceEntryDto).enqueue(new Callback<AttendanceEntryDto>() {
            @Override
            public void onResponse(@NonNull Call<AttendanceEntryDto> call, @NonNull Response<AttendanceEntryDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<AttendanceEntryDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<Boolean> deleteAttendanceEntry(Long entryId) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.deleteAttendanceEntry(entryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                success.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                success.setValue(false);
            }
        });
        return success;
    }

    // Métodos para confirmar presença e marcar não comparecimento (usando endpoints hipotéticos)
    public LiveData<AttendanceEntryDto> confirmAttendance(Long entryId) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.confirmAttendance(entryId).enqueue(new Callback<AttendanceEntryDto>() {
            @Override
            public void onResponse(@NonNull Call<AttendanceEntryDto> call, @NonNull Response<AttendanceEntryDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<AttendanceEntryDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<AttendanceEntryDto> markAsNoShow(Long entryId) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.markAsNoShow(entryId).enqueue(new Callback<AttendanceEntryDto>() {
            @Override
            public void onResponse(@NonNull Call<AttendanceEntryDto> call, @NonNull Response<AttendanceEntryDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<AttendanceEntryDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }
}