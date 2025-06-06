package br.com.projetoIntegrador.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.repository.AttendanceEntryRepository;

public class AttendanceEntryViewModel extends AndroidViewModel { // Era FilaViewModel
    private AttendanceEntryRepository attendanceEntryRepository;

    public AttendanceEntryViewModel(@NonNull Application application) {
        super(application);
        this.attendanceEntryRepository = new AttendanceEntryRepository();
    }

    public LiveData<List<AttendanceEntryDto>> listAllAttendanceEntries() {
        return attendanceEntryRepository.listAllAttendanceEntries();
    }

    public LiveData<AttendanceEntryDto> getAttendanceEntryById(Long entryId) {
        return attendanceEntryRepository.getAttendanceEntryById(entryId);
    }

    public LiveData<AttendanceEntryDto> createAttendanceEntry(AttendanceEntryDto attendanceEntryDto) {
        return attendanceEntryRepository.createAttendanceEntry(attendanceEntryDto);
    }

    public LiveData<AttendanceEntryDto> updateAttendanceEntry(Long entryId, AttendanceEntryDto attendanceEntryDto) {
        return attendanceEntryRepository.updateAttendanceEntry(entryId, attendanceEntryDto);
    }

    public LiveData<Boolean> deleteAttendanceEntry(Long entryId) {
        return attendanceEntryRepository.deleteAttendanceEntry(entryId);
    }

    // Métodos para confirmar e marcar não comparecimento
    public LiveData<AttendanceEntryDto> confirmAttendance(Long entryId) {
        return attendanceEntryRepository.confirmAttendance(entryId);
    }

    public LiveData<AttendanceEntryDto> markAsNoShow(Long entryId) {
        return attendanceEntryRepository.markAsNoShow(entryId);
    }
}