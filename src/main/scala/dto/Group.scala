package dto


case class Group(page: String, results: Seq[Movie], total_pages: Int, total_results: Int)
