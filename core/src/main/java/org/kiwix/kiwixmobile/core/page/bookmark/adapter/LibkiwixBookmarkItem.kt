/*
 * Kiwix Android
 * Copyright (c) 2023 Kiwix <android.kiwix.org>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.kiwix.kiwixmobile.core.page.bookmark.adapter

import org.kiwix.kiwixmobile.core.dao.entities.BookmarkEntity
import org.kiwix.kiwixmobile.core.page.adapter.Page
import org.kiwix.kiwixmobile.core.reader.ZimFileReader
import org.kiwix.libkiwix.Book
import org.kiwix.libkiwix.Bookmark

data class LibkiwixBookmarkItem(
  val databaseId: Long = 0L,
  override val zimId: String,
  val zimName: String,
  override val zimFilePath: String?,
  val bookmarkUrl: String,
  override val title: String,
  override val favicon: String?,
  override var isSelected: Boolean = false,
  override val url: String = bookmarkUrl,
  override val id: Long = databaseId,
  val libKiwixBook: Book?,
) : Page {
  constructor(
    libkiwixBookmark: Bookmark,
    favicon: String?,
    zimFilePath: String?
  ) : this(
    zimId = libkiwixBookmark.bookId,
    zimName = libkiwixBookmark.bookTitle,
    zimFilePath = zimFilePath,
    bookmarkUrl = libkiwixBookmark.url,
    title = libkiwixBookmark.title,
    favicon = favicon,
    libKiwixBook = null
  )

  constructor(
    title: String,
    articleUrl: String,
    zimFileReader: ZimFileReader,
    libKiwixBook: Book
  ) : this(
    zimFilePath = zimFileReader.zimFile?.canonicalPath,
    zimId = libKiwixBook.id,
    zimName = libKiwixBook.name,
    bookmarkUrl = articleUrl,
    title = title,
    favicon = zimFileReader.favicon,
    libKiwixBook = libKiwixBook
  )

  constructor(
    bookmarkEntity: BookmarkEntity,
    libkiwixBook: Book
  ) : this(
    zimId = bookmarkEntity.zimId,
    zimFilePath = bookmarkEntity.zimFilePath,
    zimName = bookmarkEntity.zimName,
    bookmarkUrl = bookmarkEntity.bookmarkUrl,
    title = bookmarkEntity.bookmarkTitle,
    favicon = bookmarkEntity.favicon,
    libKiwixBook = libkiwixBook
  )
}
