package cn.ac.origind.uno

abstract class SpecialCard(val description: String,
                  override val shortName: String,
                  val chance: Int) : Card(CardColor.Special, CardValue.Special) {
    abstract fun invoke(desk: Desk)
}
