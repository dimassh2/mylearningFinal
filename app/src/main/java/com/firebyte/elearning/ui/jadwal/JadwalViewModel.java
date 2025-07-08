package com.firebyte.elearning.ui.jadwal;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.firebyte.elearning.data.local.JadwalRepository;
import com.firebyte.elearning.data.models.Jadwal;

import java.util.List;

public class JadwalViewModel extends AndroidViewModel {

    private final JadwalRepository repository;
    private final LiveData<List<Jadwal>> allJadwal;

    public JadwalViewModel(@NonNull Application application) {
        super(application);
        repository = new JadwalRepository(application);
        allJadwal = repository.getAllJadwal();
    }

    public LiveData<List<Jadwal>> getAllJadwal() {
        return allJadwal;
    }

    public void refresh() {
        repository.refreshJadwal();
    }
}