package com.punpuf.e_bandejao.ui_more

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.util.BookUserClickInterface
import com.punpuf.e_bandejao.util.Utils
import com.punpuf.e_bandejao.vo.BookUser
import com.punpuf.e_bandejao.vo.BookUserType
import kotlinx.android.synthetic.main.list_item_book_user.view.*
import java.text.SimpleDateFormat
import java.util.*

class BookUserRecyclerAdapter(
    val clickInterface: BookUserClickInterface
) : RecyclerView.Adapter<BookUserRecyclerAdapter.BookUserViewHolder>() {

    private var data: List<BookUser> = emptyList()

    fun updateData(newData: List<BookUser>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookUserViewHolder {
        return BookUserViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_book_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BookUserViewHolder, position: Int) {
        val bookUser = data[position]

        holder.titleTv.text = bookUser.title
        holder.descriptionTv.text = bookUser.authors
        holder.layout.setOnClickListener { clickInterface.notifyBookUserClicked(bookUser) }

        when (bookUser.type) {
            BookUserType.LOAN -> {
                if (bookUser.dueDate.isNotBlank() == true) {
                    val processedDate = try { getProcessedDate(bookUser.dueDate) } catch (e: Exception) { "" }
                    if (processedDate == "") Utils.makeViewsGone(holder.dateTv)
                    else holder.dateTv.text = processedDate
                }
            }
            BookUserType.RESERVATION -> {
                Utils.makeViewsGone(holder.dateTv)
            }
            BookUserType.HISTORY -> {
                if (bookUser.returnDate.isNotBlank() == true) {
                    val processedDate = try { getProcessedDate(bookUser.returnDate) } catch (e: Exception) { "" }
                    if (processedDate == "") Utils.makeViewsGone(holder.dateTv)
                    else holder.dateTv.text = processedDate
                }
            }
        }

    }

    override fun getItemCount(): Int = data.size

    private fun getProcessedDate(date: String): String? {
        val parsedDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date) ?: return ""
        val cal = Calendar.getInstance().apply { time = parsedDate }
        return SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(cal.time)
    }

    class BookUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: View = itemView.itemBookUserLayout
        val dateTv: TextView = itemView.itemBookUserDateTv
        val titleTv: TextView = itemView.itemBookUserTitleTv
        val descriptionTv: TextView = itemView.itemBookUserDescriptionTv
    }

}