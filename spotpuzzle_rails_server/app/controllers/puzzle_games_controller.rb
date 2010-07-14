class PuzzleGamesController < ApplicationController
  # GET /puzzle_games
  # GET /puzzle_games.xml
  def index
    @fastest_puzzle_games = PuzzleGame.fastest
    @slowest_puzzle_games = PuzzleGame.slowest
    @fewest_swaps_puzzle_games = PuzzleGame.fewest_swaps
    @most_swaps_puzzle_games = PuzzleGame.most_swaps
    @puzzle_games = PuzzleGame.latest

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @puzzle_games }
    end
  end

  # GET /puzzle_games/1
  # GET /puzzle_games/1.xml
  def show
    redirect_to '/'
    #    @puzzle_game = PuzzleGame.find(params[:id])
    #
    #    respond_to do |format|
    #      format.html # show.html.erb
    #      format.xml  { render :xml => @puzzle_game }
    #    end
  end

  # GET /puzzle_games/new
  # GET /puzzle_games/new.xml
  def new
    #    @puzzle_game = PuzzleGame.new
    #
    #    respond_to do |format|
    #      format.html # new.html.erb
    #      format.xml  { render :xml => @puzzle_game }
    #    end
    redirect_to '/'
  end

  # GET /puzzle_games/1/edit
  def edit
    #@puzzle_game = PuzzleGame.find(params[:id])
    redirect_to '/'
  end

  # POST /puzzle_games
  # POST /puzzle_games.xml
  def create
    @puzzle_game = PuzzleGame.new(params[:puzzle_game])

    respond_to do |format|
      if @puzzle_game.save
        format.html { redirect_to(@puzzle_game, :notice => 'PuzzleGame was successfully created.') }
        format.xml  { render :xml => @puzzle_game, :status => :created, :location => @puzzle_game }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @puzzle_game.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /puzzle_games/1
  # PUT /puzzle_games/1.xml
  def update
    redirect_to '/'
    #    @puzzle_game = PuzzleGame.find(params[:id])
    #
    #    respond_to do |format|
    #      if @puzzle_game.update_attributes(params[:puzzle_game])
    #        format.html { redirect_to(@puzzle_game, :notice => 'PuzzleGame was successfully updated.') }
    #        format.xml  { head :ok }
    #      else
    #        format.html { render :action => "edit" }
    #        format.xml  { render :xml => @puzzle_game.errors, :status => :unprocessable_entity }
    #      end
    #    end
  end

  # DELETE /puzzle_games/1
  # DELETE /puzzle_games/1.xml
  def destroy
    redirect_to '/'
    #    @puzzle_game = PuzzleGame.find(params[:id])
    #    @puzzle_game.destroy
    #
    #    respond_to do |format|
    #      format.html { redirect_to(puzzle_games_url) }
    #      format.xml  { head :ok }
    #    end
  end
end