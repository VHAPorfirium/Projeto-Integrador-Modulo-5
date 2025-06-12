package br.com.projetoIntegrador.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceEntryRepository {

    private static final String TAG = "RepoAttendanceEntry";
    private final ApiService apiService;

    public AttendanceEntryRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<List<AttendanceEntryDto>> listAllAttendanceEntries() {
        MutableLiveData<List<AttendanceEntryDto>> result = new MutableLiveData<>();
        apiService.listAllAttendanceEntries().enqueue(new Callback<List<AttendanceEntryDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AttendanceEntryDto>> call,
                                   @NonNull Response<List<AttendanceEntryDto>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "listAllEntries → sucesso, count="
                            + (response.body() != null ? response.body().size() : "null"));
                    result.postValue(response.body());
                } else {
                    String errorMsg = "(no body)";
                    try {
                        errorMsg = response.errorBody() != null
                                ? response.errorBody().string()
                                : errorMsg;
                    } catch (IOException ignored) {}
                    Log.e(TAG, "listAllEntries → HTTP "
                            + response.code() + " / " + errorMsg);
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AttendanceEntryDto>> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "listAllEntries → falha na chamada", t);
                result.postValue(null);
            }
        });
        return result;
    }

    public LiveData<AttendanceEntryDto> getAttendanceEntryById(Long entryId) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.getAttendanceEntryById(entryId).enqueue(new Callback<AttendanceEntryDto>() {
            @Override
            public void onResponse(@NonNull Call<AttendanceEntryDto> call,
                                   @NonNull Response<AttendanceEntryDto> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "getEntryById → sucesso, id=" + entryId);
                    result.postValue(response.body());
                } else {
                    String errorMsg = "(no body)";
                    try {
                        errorMsg = response.errorBody() != null
                                ? response.errorBody().string()
                                : errorMsg;
                    } catch (IOException ignored) {}
                    Log.e(TAG, "getEntryById → HTTP "
                            + response.code() + " / " + errorMsg);
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AttendanceEntryDto> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "getEntryById → falha na chamada, id=" + entryId, t);
                result.postValue(null);
            }
        });
        return result;
    }

    public LiveData<AttendanceEntryDto> createAttendanceEntry(AttendanceEntryDto attendanceEntryDto) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.createAttendanceEntry(attendanceEntryDto)
                .enqueue(new Callback<AttendanceEntryDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AttendanceEntryDto> call,
                                           @NonNull Response<AttendanceEntryDto> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            Log.d(TAG, "createEntry → sucesso, newId=" + resp.body().getId());
                            result.postValue(resp.body());
                        } else {
                            String errorMsg = "(no body)";
                            try {
                                errorMsg = resp.errorBody() != null
                                        ? resp.errorBody().string()
                                        : errorMsg;
                            } catch (IOException ignored) {}
                            Log.e(TAG, "createEntry → HTTP "
                                    + resp.code() + " / " + errorMsg);
                            result.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AttendanceEntryDto> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "createEntry → falha na chamada", t);
                        result.postValue(null);
                    }
                });
        return result;
    }

    public LiveData<AttendanceEntryDto> updateAttendanceEntry(Long entryId, AttendanceEntryDto attendanceEntryDto) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.updateAttendanceEntry(entryId, attendanceEntryDto)
                .enqueue(new Callback<AttendanceEntryDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AttendanceEntryDto> call,
                                           @NonNull Response<AttendanceEntryDto> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            Log.d(TAG, "updateEntry → sucesso, id=" + entryId);
                            result.postValue(resp.body());
                        } else {
                            String errorMsg = "(no body)";
                            try {
                                errorMsg = resp.errorBody() != null
                                        ? resp.errorBody().string()
                                        : errorMsg;
                            } catch (IOException ignored) {}
                            Log.e(TAG, "updateEntry → HTTP "
                                    + resp.code() + " / " + errorMsg);
                            result.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AttendanceEntryDto> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "updateEntry → falha na chamada, id=" + entryId, t);
                        result.postValue(null);
                    }
                });
        return result;
    }

    public LiveData<Boolean> deleteAttendanceEntry(Long entryId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        apiService.deleteAttendanceEntry(entryId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "deleteEntry → sucesso, id=" + entryId);
                            result.postValue(true);
                        } else {
                            String errorMsg = "(no body)";
                            try {
                                errorMsg = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : errorMsg;
                            } catch (IOException ignored) {}
                            Log.e(TAG, "deleteEntry → HTTP "
                                    + response.code() + " / " + errorMsg);
                            result.postValue(false);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "deleteEntry → falha na chamada, id=" + entryId, t);
                        result.postValue(false);
                    }
                });
        return result;
    }

    public LiveData<AttendanceEntryDto> confirmAttendance(Long entryId) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.confirmAttendance(entryId)
                .enqueue(new Callback<AttendanceEntryDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AttendanceEntryDto> call,
                                           @NonNull Response<AttendanceEntryDto> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            Log.d(TAG, "confirmAttendance → sucesso, id=" + entryId);
                            result.postValue(resp.body());
                        } else {
                            String errorMsg = "(no body)";
                            try {
                                errorMsg = resp.errorBody() != null
                                        ? resp.errorBody().string()
                                        : errorMsg;
                            } catch (IOException ignored) {}
                            Log.e(TAG, "confirmAttendance → HTTP "
                                    + resp.code() + " / " + errorMsg);
                            result.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AttendanceEntryDto> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "confirmAttendance → falha na chamada, id=" + entryId, t);
                        result.postValue(null);
                    }
                });
        return result;
    }

    public LiveData<AttendanceEntryDto> markAsNoShow(Long entryId) {
        MutableLiveData<AttendanceEntryDto> result = new MutableLiveData<>();
        apiService.markAsNoShow(entryId)
                .enqueue(new Callback<AttendanceEntryDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AttendanceEntryDto> call,
                                           @NonNull Response<AttendanceEntryDto> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            Log.d(TAG, "markAsNoShow → sucesso, id=" + entryId);
                            result.postValue(resp.body());
                        } else {
                            String errorMsg = "(no body)";
                            try {
                                errorMsg = resp.errorBody() != null
                                        ? resp.errorBody().string()
                                        : errorMsg;
                            } catch (IOException ignored) {}
                            Log.e(TAG, "markAsNoShow → HTTP "
                                    + resp.code() + " / " + errorMsg);
                            result.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AttendanceEntryDto> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "markAsNoShow → falha na chamada, id=" + entryId, t);
                        result.postValue(null);
                    }
                });
        return result;
    }
}
