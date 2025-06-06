package br.com.projetoIntegrador.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import br.com.projetoIntegrador.network.NotificationRequest;
import br.com.projetoIntegrador.repository.NotificationRepository;

public class NotificationViewModel extends AndroidViewModel {
    private NotificationRepository notificationRepository;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        notificationRepository = new NotificationRepository();
    }

    public LiveData<String> sendNotification(NotificationRequest request) {
        return notificationRepository.sendNotification(request);
    }
}