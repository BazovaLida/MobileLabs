package ua.kpi.comsys.iv8101;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ListViewController extends Fragment implements BooksAdapter.OnBookListener  {
    private static final ArrayList<Book> library = new ArrayList<>();
    private final ArrayList<Book> tempLibrary = new ArrayList<>();
    private static BooksAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.frag_third, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        toolbar.setTitle("Search");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        setUpRecyclerView(recyclerView, view);
    }

    private void setUpRecyclerView(RecyclerView recyclerView, View view) {
        Context context = requireActivity().getApplicationContext();
        if(library.isEmpty())
            createLibrary(context);
        adapter = new BooksAdapter(library, this);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(context, R.drawable.ic_delete);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Book deleted;
                deleted = tempLibrary.get(position);
                tempLibrary.remove(deleted);
                library.remove(deleted);
                adapter.changeList(tempLibrary);

                String notification = "Deleted book " + deleted.getTitle();
                Toast.makeText(getContext(), notification, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }
    private void createLibrary(Context context) {
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.bookslist));
        try {
            String data = scanner.nextLine();
            JSONObject jsonObject = new JSONObject(data);

            JSONArray booksInJSON = jsonObject.getJSONArray("books");
            for (int i = 0; i < booksInJSON.length(); i++) {
                JSONObject c = booksInJSON.getJSONObject(i);
                String title = c.getString("title");
                String subtitle = c.getString("subtitle");
                String isbn13 = c.getString("isbn13");
                String price = c.getString("price");
                String image = c.getString("image").toLowerCase();


                int formatIndex = image.lastIndexOf(".");
                if(formatIndex == -1)
                    formatIndex = 0;
                String img = image.substring(0, formatIndex);

                int imageID = getResources().getIdentifier(img, "drawable", getContext().getPackageName());
                library.add(new Book(title, subtitle, isbn13, price, imageID));
            }
        } catch (JSONException e) {
            Toast.makeText(context, "JSON exception!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (NoSuchElementException e){
            Toast.makeText(context, "Exception while scanning file!", Toast.LENGTH_SHORT).show();
        }
        tempLibrary.addAll(library);
    }
    public static void addBook(Book book){
        library.add(book);
        adapter.changeList(library);
    }

    @Override
    public void onBookClick(int position) {
        String isbn = tempLibrary.get(position).getIsbn13();
        Resources res  = getContext().getResources();
        int infoId = res.getIdentifier(isbn, "raw", getContext().getPackageName());

        if(infoId != 0) {
            Intent intent = new Intent(requireActivity(), BookActivity.class);
            intent.putExtra("file", infoId);
            startActivity(intent);
        }
    }

    // calling on create option menu layout to inflate our menu file.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // inside inflater we are inflating our menu file.
        menuInflater.inflate(R.menu.search_menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tempLibrary.clear();
                for (Book item : library) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (item.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        // if the item is matched, adding it to our filtered list.
                        tempLibrary.add(item);
                    }
                }
                if (tempLibrary.isEmpty()) {
                    Toast.makeText(getContext(), "No Book Found.", Toast.LENGTH_SHORT).show();
                }
                adapter.changeList(tempLibrary);
                return false;
            }
        });

        MenuItem addItem = menu.findItem(R.id.actionAddBook);
        addItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(requireActivity(), AddBookActivity.class);
                startActivity(intent);
                return true;
            }
        });

    }

}