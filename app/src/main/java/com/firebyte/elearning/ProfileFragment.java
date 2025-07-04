package com.firebyte.elearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.firebyte.elearning.databinding.FragmentProfileBinding;
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

        binding.logoutButton.setOnClickListener(v -> logout());

        // PERBAIKAN: Menggunakan ID tombol yang benar (fab_edit_profile)
        binding.fabEditProfile.setOnClickListener(v -> toggleEditMode(true));

        binding.btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadUserProfile() {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!isAdded() || getContext() == null || binding == null) return;

            if (documentSnapshot.exists()) {
                currentUserData = documentSnapshot.toObject(User.class);
                if (currentUserData != null) {
                    // Mengisi data ke tampilan profil yang baru
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

        int viewMode = isEditing ? View.GONE : View.VISIBLE;
        int editMode = isEditing ? View.VISIBLE : View.GONE;

        // Sembunyikan/Tampilkan CardView dan informasi utama
        for (int i = 0; i < binding.getRoot().getChildCount(); i++) {
            View child = binding.getRoot().getChildAt(i);
            if (child instanceof CardView || child.getId() == R.id.profile_name || child.getId() == R.id.profile_email) {
                child.setVisibility(viewMode);
            }
        }

        // Tampilkan/Sembunyikan EditTexts dan tombol Simpan
        binding.editProfileName.setVisibility(editMode);
        binding.editProfileNim.setVisibility(editMode);
        binding.editProfileJurusan.setVisibility(editMode);
        binding.editProfileKelas.setVisibility(editMode);
        binding.editProfileBio.setVisibility(editMode);
        binding.editProfileAlamat.setVisibility(editMode);
        binding.btnSaveProfile.setVisibility(editMode);

        // Tombol FAB Edit hanya terlihat saat tidak dalam mode edit
        binding.fabEditProfile.setVisibility(viewMode);

        if (isEditing && currentUserData != null) {
            // Isi EditText dengan data saat ini untuk diedit
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
                loadUserProfile(); // Muat ulang data yang sudah diperbarui
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