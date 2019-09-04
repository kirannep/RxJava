package com.example.rxjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.function.Predicate

class MainActivity : AppCompatActivity() {

    lateinit var disposable:Disposable

    //compositeDisposable can have serveral observers
    var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val numbersObservable = numbersObservable()
        val numbersObserver = numbersObserver()
        val animalsObservable = animalObservable()
        val animalObserver = animalObserver()
        tv_click.setOnClickListener {
            //schedulers is to decide thread where you want to work on
            //observeOn, send data to mainthread
            //subsribe, pass the data to observer
            numbersObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(numbersObserver)
            compositeDisposable.add(
            animalsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { animal -> animal.toLowerCase() }
//                .filter(object:io.reactivex.functions.Predicate<String>{
//                    override fun test(t: String): Boolean {
//                        return t.toLowerCase().startsWith("d")
//                    }
//                })
//                .subscribe(animalObserver))
                .subscribe(this::animals))
        }
    }
    private fun numbersObservable() = Observable.just(1,2,3,4,5,6,7,8)

    private fun animalObservable() = Observable.just("cat","dog","horse","lion","donkey")
    private fun animalObserver():Observer<String>{
        return object:Observer<String>{
            override fun onComplete() {
                Log.d(TAG,"all items emitted")
            }

            override fun onSubscribe(d: Disposable) {
                disposable = d
                Log.d(TAG,"onsubscribe")
            }

            override fun onNext(t: String) {
                Log.d(TAG,""+t)
            }

            override fun onError(e: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    private fun randomObserver():Observer<String>{
        return object: Observer<String>{
            override fun onComplete() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onNext(t: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(e: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    private fun numbersObserver() : Observer<Int>{
        return object : Observer<Int>{

            //When all items are omitted, this method is called
            override fun onComplete() {
                Log.d(TAG,"all items emitted")
            }

            //when observer subscribes observable, this method is called
            override fun onSubscribe(d: Disposable) {
//                disposable = d
                compositeDisposable.add(d)

                Log.d(TAG,"onsubscribe")
            }

            override fun onNext(t: Int) {
                Log.d(TAG,""+t)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG,"errormsg")
            }
        }
    }
    companion object{
        const val TAG = "MainActivity"
    }

    //disposable will break the link of observer and observable,to stop updating UI
    override fun onDestroy() {
        super.onDestroy()
//        disposable.dispose()
        compositeDisposable.clear()
    }

    private fun animals(animal :String){
        Log.d(TAG,"animal "+ animal)
    }

}
