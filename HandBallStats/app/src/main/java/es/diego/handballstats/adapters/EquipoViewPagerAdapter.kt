package es.diego.handballstats.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.diego.handballstats.activities.EquipoActivity
import es.diego.handballstats.fragments.JugadoresFragment
import es.diego.handballstats.fragments.PartidosFragment

class EquipoViewPagerAdapter(activity: EquipoActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> JugadoresFragment()
            1 -> PartidosFragment()
            else -> throw IllegalStateException("Posicion desconocida")
        }
    }

}