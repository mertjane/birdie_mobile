package com.dev.birdie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dev.birdie.managers.SwipeGestureListener;
import com.dev.birdie.models.User;
import com.dev.birdie.repositories.HomeRepository;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // UI Components - Feed
    private FrameLayout cardContainer;
    private ImageView homeMainPhoto;
    private TextView txtName, txtHoros, txtAge, txtLocation;
    private LinearLayout detailsSection;

    // UI Components - Empty State
    private LinearLayout layoutEmptyState;

    // UI Components - Match Overlay
    private RelativeLayout matchOverlay;
    private KonfettiView konfettiView;
    private ImageView imgMatchUser;
    private MaterialButton btnSendMessage;
    private TextView btnKeepSwiping;

    // Data
    private List<User> userFeed = new ArrayList<>();
    private int currentIndex = 0;
    private Integer currentUserId = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupSwipeListener(view);
        fetchCurrentUserAndLoadFeed();
    }

    private void initViews(View view) {
        // Feed Views
        cardContainer = view.findViewById(R.id.cardContainer);
        homeMainPhoto = view.findViewById(R.id.homeMainPhoto);
        txtName = view.findViewById(R.id.txt_name);
        txtHoros = view.findViewById(R.id.txt_horos);
        txtAge = view.findViewById(R.id.txt_age);
        txtLocation = view.findViewById(R.id.txt_location);
        detailsSection = view.findViewById(R.id.details_section);

        // Empty State
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // Match Overlay
        matchOverlay = view.findViewById(R.id.matchOverlay);
        konfettiView = view.findViewById(R.id.konfettiView);
        imgMatchUser = view.findViewById(R.id.imgMatchUser);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);
        btnKeepSwiping = view.findViewById(R.id.btnKeepSwiping);
    }

    private void setupSwipeListener(View view) {
        // Only allow swiping on the card container
        cardContainer.setOnTouchListener(new SwipeGestureListener(getContext(), new SwipeGestureListener.SwipeCallback() {
            @Override
            public void onSwipeLeft() {
                handleSwipe("UNLIKE");
            }

            @Override
            public void onSwipeRight() {
                handleSwipe("LIKE");
            }
        }));
    }

    private void fetchCurrentUserAndLoadFeed() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            logoutAndRedirect();
            return;
        }

        String firebaseUid = firebaseUser.getUid();
        UserRepository.getUserByFirebaseUid(firebaseUid, getActivity(), new UserRepository.OnUserFetchListener() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.getUserId() != null) {
                    currentUserId = user.getUserId();
                    loadFeed();
                } else {
                    Toast.makeText(getContext(), "Error: User profile incomplete.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to fetch user profile: " + error);
            }
        });
    }

    private void loadFeed() {
        if (currentUserId == null) return;

        HomeRepository.getFeed(currentUserId, new HomeRepository.OnFeedLoadedListener() {
            @Override
            public void onSuccess(List<User> users) {
                userFeed = users;
                currentIndex = 0;

                if (userFeed.isEmpty()) {
                    showEmptyState();
                } else {
                    showFeed();
                    showUser(userFeed.get(currentIndex));
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Feed Load Error: " + error);
                // If error, show empty state or retry button
                showEmptyState();
            }
        });
    }

    private void showUser(User user) {
        txtName.setText(user.getFullName() + ",");
        txtAge.setText(user.getAge() != null ? String.valueOf(user.getAge()) : "");
        txtHoros.setText(user.getHoroscope() != null ? " " + user.getHoroscope() : "");
        txtLocation.setText(user.getLocationPostcode());

        // Use a fade-in animation for photo loading
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder1)
                    .into(homeMainPhoto);
        } else {
            homeMainPhoto.setImageResource(R.drawable.placeholder1);
        }
    }

    private void handleSwipe(String swipeType) {
        if (userFeed.isEmpty() || currentIndex >= userFeed.size()) return;
        if (currentUserId == null) return;

        User swipedUser = userFeed.get(currentIndex);

        // Optimistic Move
        moveToNextCard();

        HomeRepository.swipeUser(currentUserId, swipedUser.getUserId(), swipeType, new HomeRepository.OnSwipeResultListener() {
            @Override
            public void onSuccess(boolean isMatch, int remainingSwipes) {
                if (isMatch) {
                    // Trigger the Vivid Match Overlay!
                    triggerMatchEffect(swipedUser);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Swipe Failed: " + error);
            }
        });
    }

    private void moveToNextCard() {
        currentIndex++;
        if (currentIndex < userFeed.size()) {
            showUser(userFeed.get(currentIndex));
        } else {
            showEmptyState();
        }
    }

    // --- STATE MANAGEMENT ---

    private void showEmptyState() {
        cardContainer.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void showFeed() {
        layoutEmptyState.setVisibility(View.GONE);
        cardContainer.setVisibility(View.VISIBLE);
    }

    // --- VIVID MATCH ANIMATION ---

    private void triggerMatchEffect(User matchedUser) {
        // 1. Show Overlay
        matchOverlay.setVisibility(View.VISIBLE);
        matchOverlay.setAlpha(0f);
        matchOverlay.animate().alpha(1f).setDuration(300).start();

        // 2. Load Matched User Photo into the circle
        if (matchedUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(matchedUser.getPhotoUrl())
                    .centerCrop()
                    .into(imgMatchUser);
        }

        // 3. Scale Animation for the Photo (Pop effect)
        ScaleAnimation popAnim = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        popAnim.setDuration(500);
        popAnim.setStartOffset(200);
        imgMatchUser.startAnimation(popAnim);

        // 4. TRIGGER CONFETTI
        EmitterConfig emitter = new Emitter(500, TimeUnit.MILLISECONDS).max(500);
        konfettiView.start(
                new PartyFactory(emitter)
                        .spread(360)
                        .shapes(Shape.Circle.INSTANCE, Shape.Square.INSTANCE)
                        .colors(java.util.Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );

        // 5. Button Listeners
        btnKeepSwiping.setOnClickListener(v -> {
            // Fade out overlay
            matchOverlay.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                matchOverlay.setVisibility(View.GONE);
            }).start();
        });

        btnSendMessage.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Navigating to chat...", Toast.LENGTH_SHORT).show();
            matchOverlay.setVisibility(View.GONE);
            // TODO: Navigate to Chat Activity
        });
    }

    private void logoutAndRedirect() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}