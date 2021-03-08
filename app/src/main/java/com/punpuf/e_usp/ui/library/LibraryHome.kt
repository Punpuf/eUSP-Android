package com.punpuf.e_usp.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.punpuf.e_usp.R
import com.punpuf.e_usp.ui.theme.AppTheme
import com.punpuf.e_usp.ui.theme.Shapes
import com.punpuf.e_usp.ui.theme.Typography
import com.punpuf.e_usp.vo.BookUser
import com.punpuf.e_usp.vo.BookUserType
import com.punpuf.e_usp.vo.Resource
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber.d
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.punpuf.e_usp.ui.components.*
import com.punpuf.e_usp.vo.RENEW_ALLOWED

@ExperimentalMaterialApi
@Composable
fun LibraryHome(
    modifier: Modifier = Modifier,
    loanList: Resource<List<BookUser>>,
    reservationList: Resource<List<BookUser>>,
    historyList: Resource<List<BookUser>>,
) {
    
    var selectedBookOfUser by rememberSaveable { mutableStateOf(BookUser()) }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    
    LibraryHome(
        modifier = modifier,
        loanList = loanList,
        reservationList = reservationList,
        historyList = historyList,
        selectedBookOfUser = selectedBookOfUser,
        onBookOfUserClicked = { selectedBookOfUser = it },
        bottomSheetScaffoldState = bottomSheetScaffoldState,
    )
}

@ExperimentalMaterialApi
@Composable
fun LibraryHome(
    modifier: Modifier = Modifier,
    loanList: Resource<List<BookUser>>,
    reservationList: Resource<List<BookUser>>,
    historyList: Resource<List<BookUser>>,
    selectedBookOfUser: BookUser,
    onBookOfUserClicked: (BookUser) -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
) {
    val scope = rememberCoroutineScope()
    
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = { BookOfUserSheet(selectedBookOfUser) },
        sheetShape = Shapes.large,
        sheetPeekHeight = 0.dp,
        backgroundColor = AppTheme.colors.uiBackground,
        sheetBackgroundColor = AppTheme.colors.uiBackground,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {

            Spacer(modifier = Modifier.statusBarsHeight(56.dp))

            if (loanList.status == Resource.Status.SUCCESS && loanList.data != null) {
                BookOfUserGroup(
                    title = stringResource(R.string.library_loan_title),
                    bookOfUserList = loanList.data,
                    onBookOfUserClicked = {
                        onBookOfUserClicked(scope, bottomSheetScaffoldState)
                        onBookOfUserClicked(it)
                        d("clicked -> $selectedBookOfUser")
                                          },
                    gradient = AppTheme.colors.gradient3_2B,
                )
            }

            if (reservationList.status == Resource.Status.SUCCESS && reservationList.data != null) {
                BookOfUserGroup(
                    title = stringResource(R.string.library_reservation_title),
                    bookOfUserList = reservationList.data,
                    onBookOfUserClicked = {
                        onBookOfUserClicked(scope, bottomSheetScaffoldState)
                        onBookOfUserClicked(it)
                                          },
                    gradient = AppTheme.colors.gradient3_2A,
                )
            }

            if (historyList.status == Resource.Status.SUCCESS && historyList.data != null) {
                BookOfUserGroup(
                    title = stringResource(R.string.library_history_title),
                    bookOfUserList = historyList.data,
                    onBookOfUserClicked = {
                        onBookOfUserClicked(scope, bottomSheetScaffoldState)
                        onBookOfUserClicked(it)
                                          },
                    gradient = AppTheme.colors.gradient2_2,
                )
            }

        }
        
    }
}

@ExperimentalMaterialApi
fun onBookOfUserClicked(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
) {
    scope.launch { 
        
        scaffoldState.bottomSheetState.expand()
    }
}

@Composable
fun SecondaryText(text: String) {
    Text(
        text = text,
        style = Typography.body1,
        color = AppTheme.colors.textHelp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        lineHeight = TextUnit.Unspecified
    )
}

// bottom sheet
@Composable
fun BookOfUserSheet(
    bookOfUser: BookUser,
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 32.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
                .width(80.dp)
                .height(4.dp)
            ,
            color = AppTheme.colors.uiBorder,

            ) {}

        Text(
            text = bookOfUser.title,
            style = Typography.h4,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.Start)
        )
        SecondaryText(bookOfUser.authors)
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryText(bookOfUser.libraryName)
        
        
        when (bookOfUser.type) {
            BookUserType.LOAN -> {
                SecondaryText(stringResource(R.string.library_loan_due_date, bookOfUser.dueDate))
                if (bookOfUser.noRenew == RENEW_ALLOWED) {
                    AppRoundedButton(
                        onClick = { /*TODO*/ },
                        buttonText = stringResource(R.string.library_loan_renew),
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
            BookUserType.RESERVATION -> {
                SecondaryText(stringResource(R.string.library_reservation_hold_seq, bookOfUser.holdSeq))
                AppRoundedButton(
                    onClick = { /*TODO*/ }, 
                    buttonText = stringResource(R.string.library_reservation_cancel),
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            
            BookUserType.HISTORY -> {
                SecondaryText(stringResource(R.string.library_history_return_date, bookOfUser.returnDate))
                SecondaryText(bookOfUser.callNo)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    
}

// title + list + divider
@Composable
fun BookOfUserGroup(
    title: String,
    bookOfUserList: List<BookUser>,
    onBookOfUserClicked: (BookUser) -> Unit,
    gradient: List<Color>,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.h6,
        color = AppTheme.colors.brand,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
            .padding(vertical = 16.dp)
            .padding(horizontal = 16.dp)
    )
    
    if (bookOfUserList.isNotEmpty()) {
        BookOfUserList(
            bookOfUserList = bookOfUserList,
            onBookOfUserClicked = onBookOfUserClicked,
            gradient = gradient,
        )
    }
    else {
        Text(
            text = stringResource(R.string.library_loan_empty),
            style = MaterialTheme.typography.body2,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        
        )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    AppDivider()
}

// Contains list for books of user
@Composable
fun BookOfUserList(
    bookOfUserList: List<BookUser>,
    onBookOfUserClicked: (BookUser) -> Unit,
    gradient: List<Color>,
) {
    val scroll = rememberScrollState(0)

    val gradientWidth = with(LocalDensity.current) {
        (4 * (HighlightCardWidth + HighlightCardPadding).toPx())
    }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
    ) {
        itemsIndexed(bookOfUserList) { index, bookOfUser ->
            BookOfUserItem(
                bookOfUser = bookOfUser,
                onBookOfUserClicked = onBookOfUserClicked,
                index = index,
                scroll = scroll.value,
                gradientWidth = gradientWidth,
                gradient = gradient,
            )
        }
    }
}

private val HighlightCardWidth = 170.dp
private val HighlightCardPadding = 16.dp

private val gradientWidth
    @Composable
    get() = with(LocalDensity.current) {
        2*(HighlightCardWidth + HighlightCardPadding).toPx()
    }

// An item of a book of user
@Composable
fun BookOfUserItem(
    bookOfUser: BookUser,
    onBookOfUserClicked: (BookUser) -> Unit,
    index: Int,
    scroll: Int,
    gradient: List<Color>,
    gradientWidth: Float,
    modifier: Modifier = Modifier,
) {
    AppCard(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        val left = index * with(LocalDensity.current) {
            (HighlightCardWidth + HighlightCardPadding).toPx()
        }
        val gradientOffset = left - (scroll / 3f)
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = { onBookOfUserClicked(bookOfUser) })
                .width(HighlightCardWidth)
                .height(150.dp)
                .offsetGradientBackground(gradient, gradientWidth, gradientOffset)
                .padding(8.dp)
        ) {
            Text(
                text = bookOfUser.title,
                overflow = TextOverflow.Clip,
                style = MaterialTheme.typography.h6,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier
                    .weight(1f)
                    //.padding(horizontal = 16.dp)
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bookOfUser.authors,
                style = MaterialTheme.typography.body2,
                color = AppTheme.colors.textHelp,
                overflow = TextOverflow.Clip,
                maxLines = 1,
                modifier = Modifier
                    //.padding(horizontal = 16.dp)
                    .align(Alignment.Start)
            )

        }
    }
}