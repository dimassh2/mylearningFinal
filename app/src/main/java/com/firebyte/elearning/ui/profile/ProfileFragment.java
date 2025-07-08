package com.firebyte.elearning.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.firebyte.elearning.R;
import com.firebyte.elearning.data.models.User;
import com.firebyte.elearning.databinding.FragmentProfileBinding;
import com.firebyte.elearning.ui.auth.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private GoogleSignInClient mGoogleSignInClient;
    private User currentUserData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            userRef = db.collection("users").document(firebaseUser.getUid());
            loadUserProfile();
        }

        if (getActivity() != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        }

        // --- PERUBAHAN UTAMA DI SINI ---
        setupToolbarMenu(); // Panggil method baru untuk setup menu

        // HAPUS LISTENER TOMBOL LAMA
        // binding.logoutButton.setOnClickListener(v -> logout());
        // binding.btnEditProfile.setOnClickListener(v -> toggleEditMode(true));

        // Listener untuk tombol simpan tetap sama
        binding.btnSaveProfile.setOnClickListener(v -> {
            saveProfileChanges();
        });
    }

    private void setupToolbarMenu() {
        if (binding == null) return;
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit_profile) {
                // Panggil fungsi untuk masuk ke mode edit
                toggleEditMode(true);
                return true;
            } else if (itemId == R.id.action_logout) {
                // Panggil fungsi logout
                logout();
                return true;
            }
            return false;
        });
    }

    private void loadUserProfile() {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!isAdded() || getContext() == null || binding == null) return;

            if (documentSnapshot.exists()) {
                currentUserData = documentSnapshot.toObject(User.class);
                if (currentUserData != null) {
                    binding.profileName.setText(currentUserData.getNama());
                    binding.profileEmail.setText(currentUserData.getEmail());
                    binding.profileNim.setText("NIM: " + currentUserData.getNim());
                    binding.profileJurusan.setText("Jurusan: " + currentUserData.getJurusan());
                    binding.profileKelas.setText("Kelas: " + currentUserData.getKelas());
                    binding.profileBio.setText("Bio: " + currentUserData.getBio());
                    binding.profileAlamat.setText("Alamat: " + currentUserData.getAlamat());

                    Glide.with(this)
                            .load(currentUserData.getFotoUrl())
                            .placeholder(R.drawable.ic_profile)
                            .into(binding.profileImage);
                }
            }
        }).addOnFailureListener(e -> {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleEditMode(boolean isEditing) {
        if (binding == null) return;

        int viewModeVisibility = isEditing ? View.GONE : View.VISIBLE;
        int editModeVisibility = isEditing ? View.VISIBLE : View.GONE;

        binding.viewProfileContainer.setVisibility(viewModeVisibility);

        // HAPUS REFERENSI KE buttonContainer YANG SUDAH DIHAPUS
        // binding.buttonContainer.setVisibility(viewModeVisibility);

        binding.editProfileContainer.setVisibility(editModeVisibility);

        if (isEditing && currentUserData != null) {
            binding.editProfileName.setText(currentUserData.getNama());
            binding.editProfileNim.setText(currentUserData.getNim());
            binding.editProfileJurusan.setText(currentUserData.getJurusan());
            binding.editProfileKelas.setText(currentUserData.getKelas());
            binding.editProfileBio.setText(currentUserData.getBio());
            binding.editProfileAlamat.setText(currentUserData.getAlamat());
        }
    }

    private void saveProfileChanges() {
        if (binding == null) return;
        String newName = binding.editProfileName.getText().toString().trim();
        String newNim = binding.editProfileNim.getText().toString().trim();
        String newJurusan = binding.editProfileJurusan.getText().toString().trim();
        String newKelas = binding.editProfileKelas.getText().toString().trim();
        String newBio = binding.editProfileBio.getText().toString().trim();
        String newAlamat = binding.editProfileAlamat.getText().toString().trim();

        if (newName.isEmpty()) {
            binding.editProfileName.setError("Nama tidak boleh kosong");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nama", newName);
        updates.put("nim", newNim);
        updates.put("jurusan", newJurusan);
        updates.put("kelas", newKelas);
        updates.put("bio", newBio);
        updates.put("alamat", newAlamat);

        userRef.update(updates).addOnSuccessListener(aVoid -> {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                toggleEditMode(false);
                loadUserProfile();
            }
        }).addOnFailureListener(e -> {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        if (getActivity() == null || mGoogleSignInClient == null) return;
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}