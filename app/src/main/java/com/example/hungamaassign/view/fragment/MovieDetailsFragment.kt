package com.example.hungamaassign.view.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hungamaassign.InternetCheck
import com.example.hungamaassign.R
import com.example.hungamaassign.view.adapter.CastRecyclerViewAdapter
import com.example.hungamaassign.view.adapter.SimilarRecyclerViewAdapter
import com.example.hungamaassign.viewModel.DetailsViewModel
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.fragment_movie_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [MovieDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovieDetailsFragment : Fragment(), Player.EventListener {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    lateinit var tvTitle: TextView;
    lateinit var tvReleaseDate: TextView;
    lateinit var tvLanguage: TextView;
    lateinit var tvOverview: TextView;
    private lateinit var viewModel: DetailsViewModel
    private var movieId:Int = 0
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var adapterCast : CastRecyclerViewAdapter
    private lateinit var adapterCrew : CastRecyclerViewAdapter
    private lateinit var adapterSimilar : SimilarRecyclerViewAdapter

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private val mp4Url = "https://html5demos.com/assets/dizzy.mp4"
    private val dashUrl = "https://storage.googleapis.com/wvmedia/clear/vp9/tears/tears_uhd.mpd"
    private val dashUrl2 = "https://storage.googleapis.com/wvmedia/clear/vp9/tears/tears_uhd.mpd"
    private val urlList = listOf(mp4Url to "default", dashUrl to "dash", dashUrl2 to "dash")

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(context, "exoplayer-sample")
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MovieDetailsFragment()
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.searchFragment).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_movie_details, container, false)
        setHasOptionsMenu(true)

        tvTitle = view.findViewById(R.id.tv_title)
        tvReleaseDate = view.findViewById(R.id.tv_date)
        tvLanguage = view.findViewById(R.id.tv_language)
        tvOverview = view.findViewById(R.id.tv_genre_details)

        //load data from get argument
        tvTitle.text = arguments?.getString("title")
        tvReleaseDate.text = "Release Date: " +arguments?.getString("release_date")
        tvLanguage.text = "Language: " +arguments?.getString("language")
        tvOverview.text = arguments?.getString("overview")

        movieId = arguments?.getInt("id")!!

        val recyclerView = view.findViewById<RecyclerView>(R.id.list_cast)
        adapterCast = CastRecyclerViewAdapter()
        recyclerView.adapter = adapterCast
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)

        val recyclerViewCrew = view.findViewById<RecyclerView>(R.id.list_crew)
        adapterCrew = CastRecyclerViewAdapter()
        recyclerViewCrew.adapter = adapterCrew
        recyclerViewCrew.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


        val recyclerViewSimilar = view.findViewById<RecyclerView>(R.id.list_similar)
        adapterSimilar = SimilarRecyclerViewAdapter()
        recyclerViewSimilar.adapter = adapterSimilar
        recyclerViewSimilar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


        return view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)

        scope.launch {
            InternetCheck(object : InternetCheck.Consumer {
                override fun accept(internet: Boolean?) {
                    Log.d("test", "check")
                    if (internet!!){
                        viewModel.getCastList(movieId, "cast");
                        viewModel.getCastList(movieId, "crew");
                        viewModel.getSimilarList(movieId)
                    }

                }
            })

        }

        viewModel.allCastList.observe(this, Observer {
            list ->
            //set data
            adapterCast.setCastList(list)
        })

        viewModel.allCrewList.observe(this,{
            //set data
            list -> adapterCrew.setCastList(list)
        })

        viewModel.allSimilarList.observe(this, {
            //set data
            list -> adapterSimilar.setSimilarList(list)
        })
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = context?.let { SimpleExoPlayer.Builder(it).build() }!!
//        val randomUrl =  urlList.random()
        preparePlayer(mp4Url, "default")
//        preparePlayer(randomUrl.first, randomUrl.second)
        exoplayerView.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
    }

    private fun buildMediaSource(uri: Uri, type: String): MediaSource {
        //prepare mediasource depend on type (hls, dash or mp4)
        return if (type == "dash") {
            DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        }
    }

    private fun preparePlayer(videoUrl: String, type: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri, type)
        simpleExoplayer.prepare(mediaSource)
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            progressBar.visibility = View.INVISIBLE
    }

}