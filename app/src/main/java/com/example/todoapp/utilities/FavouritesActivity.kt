package com.example.todoapp.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.NotificationReceiver
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityFavouritesBinding
import com.example.todoapp.itemSheets.NewNoteSheet
import com.example.todoapp.itemSheets.NewTaskSheet
import com.example.todoapp.messageExtra
import com.example.todoapp.titleExtra
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter
import com.example.todoapp.ui.home.TaskItem.TaskItemClickListener
import com.example.todoapp.ui.notes.AudioNoteItem.AudioNoteItem
import com.example.todoapp.ui.notes.AudioNoteItem.AudioNoteItemAdapter
import com.example.todoapp.ui.notes.AudioNoteItem.AudioNoteItemClickListener
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import com.example.todoapp.ui.notes.NoteItem.NoteItemAdapter
import com.example.todoapp.ui.notes.NoteItem.NoteItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.SimpleDateFormat
import java.time.LocalDate

class FavouritesActivity : AppCompatActivity(), TaskItemClickListener, NoteItemClickListener, AudioNoteItemClickListener {

    private lateinit var binding: ActivityFavouritesBinding

    private lateinit var tasksDatabaseRef: DatabaseReference
    private lateinit var notesDatabaseRef: DatabaseReference
    private lateinit var audioNotesDatabaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskList: ArrayList<TaskItem>
    private lateinit var notesList: ArrayList<NoteItem>
    private lateinit var audioNotesList: ArrayList<AudioNoteItem>

    private var viewMod = "tasks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        tasksDatabaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        notesDatabaseRef = FirebaseDatabase.getInstance().reference.child("NoteItems").child(auth.currentUser?.uid.toString())
        audioNotesDatabaseRef = FirebaseDatabase.getInstance().reference.child("AudioNoteItems").child(auth.currentUser?.uid.toString())

        taskList = arrayListOf()
        notesList = arrayListOf()
        audioNotesList = arrayListOf()

        deleteSwipe()

        val activeColor = binding.tasksMod.currentTextColor
        val greyColor = ContextCompat.getColor(applicationContext, R.color.grey)

        tasksDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val task : TaskItem? = it.getValue(TaskItem::class.java)
                        task!!.id = it.key
                        if(task!!.isFavourite == "true")
                            taskList.add(task!!)
                    }
                }
                setRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

        notesDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val note : NoteItem? = it.getValue(NoteItem::class.java)
                        note!!.id = it.key
                        if(note!!.isFavourite == "true")
                            notesList.add(note!!)
                    }
                }
                setRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

        audioNotesDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                audioNotesList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val note : AudioNoteItem? = it.getValue(AudioNoteItem::class.java)
                        note!!.id = it.key
                        if(note!!.isFavourite == "true")
                            audioNotesList.add(note!!)
                    }
                }
                setRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

        binding.notesMod.setTextColor(greyColor)
        binding.audioNotesMod.setTextColor(greyColor)

        binding.tasksMod.setOnClickListener{
            if(viewMod != "tasks"){
                viewMod = "tasks"

                binding.tasksModUnderline.visibility = View.VISIBLE
                binding.notesModUnderline.visibility = View.GONE
                binding.audioNotesModUnderline.visibility = View.GONE

                binding.tasksMod.setTextColor(activeColor)
                binding.notesMod.setTextColor(greyColor)
                binding.audioNotesMod.setTextColor(greyColor)

                setRecycleView()
            }
        }
        binding.notesMod.setOnClickListener{
            if(viewMod != "notes"){
                viewMod = "notes"

                binding.tasksModUnderline.visibility = View.GONE
                binding.notesModUnderline.visibility = View.VISIBLE
                binding.audioNotesModUnderline.visibility = View.GONE

                binding.tasksMod.setTextColor(greyColor)
                binding.notesMod.setTextColor(activeColor)
                binding.audioNotesMod.setTextColor(greyColor)

                setRecycleView()
            }
        }
        binding.audioNotesMod.setOnClickListener{
            if(viewMod != "audio"){
                viewMod = "audio"

                binding.tasksModUnderline.visibility = View.GONE
                binding.notesModUnderline.visibility = View.GONE
                binding.audioNotesModUnderline.visibility = View.VISIBLE

                binding.tasksMod.setTextColor(greyColor)
                binding.notesMod.setTextColor(greyColor)
                binding.audioNotesMod.setTextColor(activeColor)

                setRecycleView()
            }
        }
    }

    private fun setRecycleView() {
        val activity = this
        if(viewMod == "tasks"){
            binding.favRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(taskList, activity)
            }
        }
        if(viewMod == "notes"){
            binding.favRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = NoteItemAdapter(notesList, activity)
            }
        }
        if(viewMod == "audio"){
            binding.favRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = AudioNoteItemAdapter(audioNotesList, activity)
            }
        }
    }

    private fun deleteSwipe(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                var taskItem: TaskItem? = null
                var noteItem: NoteItem? = null
                var audioNoteItem: AudioNoteItem? = null

                if(viewMod == "tasks"){
                    taskItem = taskList[position]
                }
                if(viewMod == "notes"){
                    noteItem = notesList[position]
                }
                if(viewMod == "audio"){
                    audioNoteItem = audioNotesList[position]
                }

                when (direction){
                    ItemTouchHelper.LEFT ->{

                        if(viewMod == "tasks"){
                            deleteTaskItem(taskItem!!)

                            Snackbar.make(binding.favouritesActivity,"Задача \"" + taskItem.name + "\" удалена",Snackbar.LENGTH_SHORT).apply {
                                setAction("Отмена"){
                                    if(taskItem.status == "done"){
                                        taskItem.status = "completed"
                                        updateTaskItem(taskItem)
                                    }else{
                                        val taskId = tasksDatabaseRef.push().key!!
                                        tasksDatabaseRef.child(taskId).setValue(taskItem)
                                    }
                                    if(taskItem.notificationId != 0){
                                        scheduleNotification(taskItem)
                                    }
                                }
                                show()
                            }
                        }
                        if(viewMod == "notes"){
                            deleteNoteItem(noteItem!!)

                            Snackbar.make(binding.favouritesActivity,"Заметка \"" + noteItem.title + "\" удалена",Snackbar.LENGTH_SHORT).apply {
                                setAction("Отмена"){
                                    val noteId = tasksDatabaseRef.push().key!!
                                    notesDatabaseRef.child(noteId).setValue(noteItem)
                                }
                                show()
                            }
                        }
                        if(viewMod == "audio"){
                            deleteAudioNoteItem(audioNoteItem!!)

                            Snackbar.make(binding.favouritesActivity,"Аудиозаметка \"" + audioNoteItem.title + "\" удалена",Snackbar.LENGTH_SHORT).apply {
                                setAction("Отмена"){
                                    val noteId = audioNotesDatabaseRef.push().key!!
                                    audioNotesDatabaseRef.child(noteId).setValue(audioNoteItem)
                                }
                                show()
                            }
                        }

                    }
                    ItemTouchHelper.RIGHT ->{
                        if(viewMod == "tasks"){
                            setTaskFavorite(taskItem!!)
                        }
                        if(viewMod == "notes"){
                            setNoteFavorite(noteItem!!)
                        }
                        if(viewMod == "audio"){
                            setAudioNoteFavorite(audioNoteItem!!)
                        }
                    }
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red2))
                    .addSwipeLeftCornerRadius(1, 15F)
                    .addSwipeRightActionIcon(R.drawable.baseline_star_30)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(applicationContext, R.color.fav))
                    .addSwipeRightCornerRadius(1, 15F)
                    .create()
                    .decorate()

                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

            }
        }).attachToRecyclerView(binding.favRecycleView)
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem,taskList).show(this.supportFragmentManager, "editTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        if(taskItem.status == "live"){
            taskItem.status = "completed"
            taskItem.completedDateString = LocalDate.now().toString()
        }else if(taskItem.status == "completed"){
            taskItem.status = "live"
            taskItem.completedDateString = "null"
        }
        updateTaskItem(taskItem)
        setRecycleView()
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        if(taskItem.status == "done" || taskItem.status == "live"){
            tasksDatabaseRef.child(taskItem.id.toString()).removeValue()
        }else if (taskItem.status == "completed"){
            taskItem.status = "done"
            updateTaskItem(taskItem)
        }
        if(taskItem.notificationId != 0){
            cancelScheduledNotification(taskItem.notificationId!!)
        }
    }

    override fun setTaskFavorite(taskItem: TaskItem) {
        if(taskItem.isFavourite == "false"){
            taskItem.isFavourite = "true"
        }else{
            taskItem.isFavourite = "false"
        }
        updateTaskItem(taskItem)
    }

    private fun updateTaskItem(taskItem: TaskItem) {
        val map = HashMap<String, Any>()
        map[taskItem.id.toString()] = taskItem
        tasksDatabaseRef.updateChildren(map)
    }
    private fun updateNoteItem(noteItem: NoteItem) {
        val map = HashMap<String, Any>()
        map[noteItem.id.toString()] = noteItem
        notesDatabaseRef.updateChildren(map)
    }
    private fun updateAudioNoteItem(audioNoteItem: AudioNoteItem) {
        val map = HashMap<String, Any>()
        map[audioNoteItem.id.toString()] = audioNoteItem
        audioNotesDatabaseRef.updateChildren(map)
    }

    private fun cancelScheduledNotification(id: Int) {
        val intent = Intent(this, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(this, id,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleNotification(task: TaskItem) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val title = task.name
        val message = task.desc

        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(this,task.notificationId!!,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val df = SimpleDateFormat("yyyy-MM-dd-HH:mm")
        val timeStr : String = task.dueDate + "-" + task.dueTimeString
        val time : Long = df.parse(timeStr).time

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent)
    }


    override fun deleteAudioNoteItem(audioNoteItem: AudioNoteItem) {
        audioNotesDatabaseRef.child(audioNoteItem.id.toString()).removeValue()
    }

    override fun setAudioNoteFavorite(audioNoteItem: AudioNoteItem) {
        if(audioNoteItem.isFavourite == "true"){
            audioNoteItem.isFavourite = "false"
        }else{
            audioNoteItem.isFavourite = "true"
        }
        updateAudioNoteItem(audioNoteItem)
    }

    override fun editNoteItem(noteItem: NoteItem) {
        NewNoteSheet(noteItem).show(this.supportFragmentManager, "editTaskTag")
    }

    override fun deleteNoteItem(noteItem: NoteItem) {
        notesDatabaseRef.child(noteItem.id.toString()).removeValue()
    }

    override fun setNoteFavorite(noteItem: NoteItem) {
        if(noteItem.isFavourite == "true"){
            noteItem.isFavourite = "false"
        }else{
            noteItem.isFavourite = "true"
        }
        updateNoteItem(noteItem)
    }
}